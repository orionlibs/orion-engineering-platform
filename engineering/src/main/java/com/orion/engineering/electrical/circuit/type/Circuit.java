package com.orion.engineering.electrical.circuit.type;

import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Switch;
import com.orion.engineering.electrical.circuit.component.VoltageSource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
                if(graph.vertexSet().stream()
                                .filter(c -> c instanceof Switch)
                                .filter(s -> ((Switch)s).isOff())
                                .count() > 0)
                {
                    return false;
                }
                return isVertexOnCycle(voltageSource);
            }
        }
        return false;
    }


    private boolean isVertexOnCycle(CircuitComponent start)
    {
        for(CircuitComponent other : graph.vertexSet())
        {
            if(!other.equals(start) && graph.getAllEdges(start, other).size() > 1)
            {
                return true;
            }
        }
        // DFS: try to find a path that leaves 'start' and comes back to it
        Set<CircuitComponent> visited = new HashSet<>();
        return dfsFindsPathBack(start, start, null, visited);
    }


    private boolean dfsFindsPathBack(
                    CircuitComponent target,
                    CircuitComponent current,
                    TerminalToTerminalConnection arrivedVia,   // edge we used to get here — don't backtrack on it
                    Set<CircuitComponent> visited)
    {
        visited.add(current);
        for(TerminalToTerminalConnection edge : graph.edgesOf(current))
        {
            // Skip the exact edge we arrived on to avoid trivially reversing one step
            if(edge.equals(arrivedVia))
            {
                continue;
            }
            CircuitComponent neighbor = graph.getEdgeSource(edge).equals(current)
                            ? graph.getEdgeTarget(edge)
                            : graph.getEdgeSource(edge);
            // We reached the start again via a different path — cycle confirmed
            if(neighbor.equals(target) && arrivedVia != null)
            {
                return true;
            }
            if(!visited.contains(neighbor))
            {
                if(dfsFindsPathBack(target, neighbor, edge, visited))
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
