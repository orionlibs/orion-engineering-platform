package com.orion.engineering.electrical.circuit;

import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.VoltageSource;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;

@Getter
@Builder
@AllArgsConstructor
public class Circuit
{
    private final Graph<CircuitComponent, TerminalToTerminalConnection> graph;


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
                PatonCycleBase<CircuitComponent, TerminalToTerminalConnection> cycleFinder = new PatonCycleBase<>(graph);
                CycleBasis<CircuitComponent, TerminalToTerminalConnection> basis = cycleFinder.getCycleBasis();
                return basis.getCycles()
                                .stream()
                                .anyMatch(cycle -> cycle.contains(voltageSource));
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }


    public void printCircuit()
    {
        System.out.println("=== Circuit Components ===");
        graph.vertexSet().forEach(v -> System.out.println("  " + v.getName()));
        System.out.println("\n=== Connections ===");
        graph.edgeSet().forEach(e -> {
            CircuitComponent src = graph.getEdgeSource(e);
            CircuitComponent tgt = graph.getEdgeTarget(e);
            System.out.printf("  %s.%s ──── %s.%s%s%n",
                            src.getName(), e.getFromTerminal().getName(),
                            tgt.getName(), e.getToTerminal().getName(),
                            e.getFromTerminal() != null ? "  [" + e.getFromTerminal().getName() + "]" : "");
        });
    }
}
