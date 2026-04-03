package com.orion.engineering.electrical.circuit;

import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.VoltageSource;
import com.orion.engineering.electrical.circuit.component.Wire;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jgrapht.Graph;

@Getter
@Builder
@AllArgsConstructor
public class Circuit
{
    private final Graph<CircuitComponent, TerminalToTerminalConnection> graph;


    public boolean isThereVoltageSource()
    {
        return components.stream().anyMatch(c -> c instanceof VoltageSource);
    }


    public boolean isOpen()
    {
        return !isClosed();
    }


    public boolean isClosed()
    {
        Optional<CircuitComponent> voltageSourceWrap = components.stream()
                        .filter(c -> c instanceof VoltageSource)
                        .findFirst();
        if(voltageSourceWrap.isPresent())
        {
            VoltageSource voltageSource = (VoltageSource)voltageSourceWrap.get();
            if(voltageSource.getVoltage().getValue() > 0.0d)
            {
                for(TerminalToTerminalConnection connection : connections)
                {
                    Wire wireConnectedToVoltageSourceNegativeTerminal = null;
                    if(connection.getFromTerminal() == voltageSource.getNegativeTerminal())
                    {
                        for(CircuitComponent component : components)
                        {
                            if(component instanceof Wire wire)
                            {
                                if(wire.getPositiveTerminal() == connection.getToTerminal() || wire.getNegativeTerminal() == connection.getToTerminal())
                                {
                                    wireConnectedToVoltageSourceNegativeTerminal = wire;
                                }
                            }
                        }
                    }
                }
                return true;
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


    private Set<CircuitComponent> findComponentsConnectedTo(CircuitComponent component)
    {
        Set<CircuitComponent> connectedComponents = new HashSet<>();

        for(TerminalToTerminalConnection connection : connections)
        {
            if(connection.getFromTerminal() == component.get)
            {
                for(CircuitComponent component : components)
                {
                    if(component instanceof Wire wire)
                    {
                        if(wire.getPositiveTerminal() == connection.getToTerminal() || wire.getNegativeTerminal() == connection.getToTerminal())
                        {
                            wireConnectedToVoltageSourceNegativeTerminal = wire;
                        }
                    }
                }
            }
        }

        return connectedComponents;
    }


    public void printCircuit() {
        System.out.println("=== Circuit Components ===");
        graph.vertexSet().forEach(v -> System.out.println("  " + v));
        System.out.println("\n=== Connections ===");
        graph.edgeSet().forEach(e -> {
            CircuitComponent src = graph.getEdgeSource(e);
            CircuitComponent tgt = graph.getEdgeTarget(e);
            System.out.printf("  %s.%s ──── %s.%s%s%n",
                            src.getName(), e.getFromTerminal(),
                            tgt.getId(), e.getToTerminal(),
                            e.getNetName() != null ? "  [" + e.getNetName() + "]" : "");
        });
    }
}
