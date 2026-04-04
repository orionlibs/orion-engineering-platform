package com.orion.engineering.electrical.circuit.type;

import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Switch;
import com.orion.engineering.electrical.circuit.component.VoltageSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.jgrapht.Graph;

@Getter
@SuperBuilder
@AllArgsConstructor
public abstract class Circuit
{
    protected final Graph<CircuitComponent, TerminalToTerminalConnection> graph;
    protected boolean isRunning;
    protected StopWatch stopwatch;
    protected long lastRunDuration;
    protected long lastRunStartTime;
    protected long lastRunStopTime;
    // -------------------------------------------------------------------------
    // Topology queries
    // -------------------------------------------------------------------------


    public boolean isThereVoltageSource()
    {
        return graph.vertexSet().stream().anyMatch(c -> c instanceof VoltageSource);
    }


    public boolean isOpen()
    {
        return !isClosed();
    }


    public boolean isClosed()
    {
        Optional<CircuitComponent> voltageSourceWrap = graph.vertexSet().stream()
                        .filter(c -> c instanceof VoltageSource)
                        .findFirst();
        if(voltageSourceWrap.isPresent())
        {
            VoltageSource voltageSource = (VoltageSource)voltageSourceWrap.get();
            if(voltageSource.getVoltage().getValue() > 0.0d)
            {
                Set<CircuitComponent> excluded = graph.vertexSet().stream()
                                .filter(c -> c instanceof Switch && ((Switch)c).isOff())
                                .collect(Collectors.toSet());
                return isVertexOnCycle(voltageSource, excluded);
            }
        }
        return false;
    }
    // -------------------------------------------------------------------------
    // Series detection
    //
    // Two components are in series when they belong to the same unbroken chain
    // of degree-2 vertices. A degree-2 vertex has exactly one way in and one
    // way out, meaning no branching occurs at that point.
    //
    // Example:  battery(2) — wire1(2) — lamp(2) — wire2(2) — battery
    //           → all four are in the same series group
    // -------------------------------------------------------------------------


    public boolean areInSeries(CircuitComponent c1, CircuitComponent c2)
    {
        return getSeriesGroups().stream()
                        .anyMatch(group -> group.contains(c1) && group.contains(c2));
    }


    public List<Set<CircuitComponent>> getSeriesGroups()
    {
        Set<CircuitComponent> visited = new HashSet<>();
        List<Set<CircuitComponent>> groups = new ArrayList<>();
        for(CircuitComponent component : graph.vertexSet())
        {
            // Only start a chain expansion from an unvisited degree-2 vertex
            if(!visited.contains(component) && graph.degreeOf(component) == 2)
            {
                Set<CircuitComponent> group = new LinkedHashSet<>();
                expandSeriesChain(component, null, group, visited);
                if(group.size() > 1)
                {
                    groups.add(group);
                }
            }
        }
        return groups;
    }


    // Walk along the chain as long as the next vertex also has degree 2
    private void expandSeriesChain(
                    CircuitComponent current,
                    CircuitComponent from,
                    Set<CircuitComponent> group,
                    Set<CircuitComponent> visited)
    {
        visited.add(current);
        group.add(current);
        for(CircuitComponent neighbor : getNeighbors(current))
        {
            if(!neighbor.equals(from)
                            && !visited.contains(neighbor)
                            && graph.degreeOf(neighbor) == 2)
            {
                expandSeriesChain(neighbor, current, group, visited);
            }
        }
    }
    // -------------------------------------------------------------------------
    // Parallel detection
    //
    // Two components are in parallel when they connect the same pair of
    // neighbour vertices (same entry point, same exit point).
    //
    // Example:  wire1 — lamp    — wire2
    //           wire1 — resistor— wire2
    //           → lamp and resistor are in parallel (both neighboured by wire1 & wire2)
    // -------------------------------------------------------------------------


    public boolean areInParallel(CircuitComponent c1, CircuitComponent c2)
    {
        Set<CircuitComponent> neighbors1 = getNeighbors(c1);
        Set<CircuitComponent> neighbors2 = getNeighbors(c2);
        // Remove each other from the neighbour sets before comparing,
        // so directly connected components are not falsely flagged
        neighbors1.remove(c2);
        neighbors2.remove(c1);
        return !neighbors1.isEmpty() && neighbors1.equals(neighbors2);
    }


    public List<Set<CircuitComponent>> getParallelGroups()
    {
        List<CircuitComponent> components = new ArrayList<>(graph.vertexSet());
        List<Set<CircuitComponent>> groups = new ArrayList<>();
        Set<CircuitComponent> assigned = new HashSet<>();
        for(int i = 0; i < components.size(); i++)
        {
            CircuitComponent c1 = components.get(i);
            if(assigned.contains(c1))
            {
                continue;
            }
            Set<CircuitComponent> group = new LinkedHashSet<>();
            group.add(c1);
            for(int j = i + 1; j < components.size(); j++)
            {
                CircuitComponent c2 = components.get(j);
                if(!assigned.contains(c2) && areInParallel(c1, c2))
                {
                    group.add(c2);
                    assigned.add(c2);
                }
            }
            if(group.size() > 1)
            {
                assigned.add(c1);
                groups.add(group);
            }
        }
        return groups;
    }
    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------


    private Set<CircuitComponent> getNeighbors(CircuitComponent component)
    {
        return graph.edgesOf(component).stream()
                        .map(e -> graph.getEdgeSource(e).equals(component)
                                        ? graph.getEdgeTarget(e)
                                        : graph.getEdgeSource(e))
                        .collect(Collectors.toSet());
    }


    private boolean isVertexOnCycle(CircuitComponent start, Set<CircuitComponent> excluded)
    {
        for(CircuitComponent other : graph.vertexSet())
        {
            if(!other.equals(start)
                            && !excluded.contains(other)
                            && graph.getAllEdges(start, other).size() > 1)
            {
                return true;
            }
        }
        Set<CircuitComponent> visited = new HashSet<>();
        return dfsFindsPathBack(start, start, null, visited, excluded);
    }


    private boolean dfsFindsPathBack(
                    CircuitComponent target,
                    CircuitComponent current,
                    TerminalToTerminalConnection arrivedVia,
                    Set<CircuitComponent> visited,
                    Set<CircuitComponent> excluded)
    {
        visited.add(current);
        for(TerminalToTerminalConnection edge : graph.edgesOf(current))
        {
            if(edge.equals(arrivedVia))
            {
                continue;
            }
            CircuitComponent neighbor = graph.getEdgeSource(edge).equals(current)
                            ? graph.getEdgeTarget(edge)
                            : graph.getEdgeSource(edge);
            if(neighbor.equals(target) && arrivedVia != null)
            {
                return true;
            }
            if(!excluded.contains(neighbor) && !visited.contains(neighbor))
            {
                if(dfsFindsPathBack(target, neighbor, edge, visited, excluded))
                {
                    return true;
                }
            }
        }
        return false;
    }
    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------


    public void start()
    {
        if(isClosed())
        {
            isRunning = true;
            stopwatch = StopWatch.createStarted();
            lastRunStartTime = System.nanoTime();
        }
    }


    public void stop()
    {
        if(isRunning)
        {
            isRunning = false;
            stopwatch.stop();
            lastRunDuration = stopwatch.getNanoTime();
            lastRunStopTime = System.nanoTime();
        }
    }
    // -------------------------------------------------------------------------
    // Debug
    // -------------------------------------------------------------------------


    public void printCircuit()
    {
        System.out.println("=== Circuit Components ===");
        graph.vertexSet().forEach(v -> System.out.println("  " + v.getName()));
        System.out.println("\n=== Connections ===");
        graph.edgeSet().forEach(e -> {
            CircuitComponent src = graph.getEdgeSource(e);
            CircuitComponent tgt = graph.getEdgeTarget(e);
            System.out.printf("%s.%s ──── %s.%s%n",
                            src.getName(), e.getFromTerminal().getName(),
                            tgt.getName(), e.getToTerminal().getName());
        });
        System.out.println("\n=== Series Groups ===");
        getSeriesGroups().forEach(group -> {
            String names = group.stream()
                            .map(CircuitComponent::getName)
                            .collect(Collectors.joining(" → "));
            System.out.println("  [ " + names + " ]");
        });
        System.out.println("\n=== Parallel Groups ===");
        getParallelGroups().forEach(group -> {
            String names = group.stream()
                            .map(CircuitComponent::getName)
                            .collect(Collectors.joining(" ∥ "));
            System.out.println("  [ " + names + " ]");
        });
    }
}