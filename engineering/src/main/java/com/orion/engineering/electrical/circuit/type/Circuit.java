package com.orion.engineering.electrical.circuit.type;

import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Switch;
import com.orion.engineering.electrical.circuit.component.VoltageSource;
import java.util.HashSet;
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
                // Build a set of vertices to exclude from traversal (off switches)
                Set<CircuitComponent> excluded = graph.vertexSet().stream()
                                .filter(c -> c instanceof Switch && ((Switch)c).isOff())
                                .collect(Collectors.toSet());
                return isVertexOnCycle(voltageSource, excluded);
            }
        }
        return false;
    }


    private boolean isVertexOnCycle(CircuitComponent start, Set<CircuitComponent> excluded)
    {
        // Multi-edge shortcut — but only if neither endpoint is excluded
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
            // Found our way back to the start — cycle confirmed
            if(neighbor.equals(target) && arrivedVia != null)
            {
                return true;
            }
            // Skip off-switches and already-visited vertices
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


    public void printCircuit()
    {
        System.out.println("=== Circuit Components ===");
        graph.vertexSet().forEach(v -> System.out.println("  " + v.getName()));
        System.out.println("\n=== Connections ===");
        graph.edgeSet().forEach(e -> {
            CircuitComponent src = graph.getEdgeSource(e);
            CircuitComponent tgt = graph.getEdgeTarget(e);
            System.out.printf("%s.%s ──── %s.%s%s%n",
                            src.getName(), e.getFromTerminal().getName(),
                            tgt.getName(), e.getToTerminal().getName(),
                            e.getFromTerminal() != null ? "  [" + e.getFromTerminal().getName() + "]" : "");
        });
    }


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
}
