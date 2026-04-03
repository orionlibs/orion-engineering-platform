package com.orion.enginering.electrical.circuit.type;

import static org.assertj.core.api.Assertions.assertThat;

import com.orion.engineering.electrical.Voltage;
import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.Battery;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Terminal;
import com.orion.engineering.electrical.circuit.component.Wire;
import com.orion.engineering.electrical.circuit.type.DirectCurrentCircuit;
import com.orion.enginering.TestBase;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.junit.jupiter.api.Test;

public class DirectCurrentCircuitTest extends TestBase
{
    @Test
    void simpleCircuit()
    {
        Battery battery = Battery.builder()
                        .name("battery")
                        .voltage(Voltage.builder()
                                        .value(1.0d)
                                        .build())
                        .positiveTerminal(Terminal.builder()
                                        .name("+")
                                        .isPositiveTerminal(true)
                                        .build())
                        .negativeTerminal(Terminal.builder()
                                        .name("-")
                                        .isPositiveTerminal(false)
                                        .build())
                        .build();
        Wire wire1 = Wire.builder()
                        .name("wire")
                        .length(1.0d)
                        .positiveTerminal(Terminal.builder()
                                        .name("+")
                                        .isPositiveTerminal(true)
                                        .build())
                        .negativeTerminal(Terminal.builder()
                                        .name("-")
                                        .isPositiveTerminal(false)
                                        .build())
                        .build();
        Graph<CircuitComponent, TerminalToTerminalConnection> graph = GraphTypeBuilder.<CircuitComponent, TerminalToTerminalConnection>undirected()
                        .allowingMultipleEdges(true)   // two components can share multiple connections
                        .allowingSelfLoops(false)
                        .edgeClass(TerminalToTerminalConnection.class)
                        .buildGraph();
        graph.addVertex(battery);
        graph.addVertex(wire1);
        graph.addEdge(battery, wire1, TerminalToTerminalConnection.builder()
                        .fromTerminal(battery.getNegativeTerminal())
                        .toTerminal(wire1.getNegativeTerminal())
                        .build());
        TerminalToTerminalConnection c2 = TerminalToTerminalConnection.builder()
                        .fromTerminal(wire1.getPositiveTerminal())
                        .toTerminal(battery.getPositiveTerminal())
                        .build();
        graph.addEdge(wire1, battery, c2);
        DirectCurrentCircuit circuit = DirectCurrentCircuit.builder()
                        .graph(graph)
                        .build();
        circuit.printCircuit();
        circuit.start();
        assertThat(circuit.isThereVoltageSource()).isTrue();
        assertThat(circuit.isClosed()).isTrue();
        graph.removeEdge(c2);
        assertThat(circuit.isOpen()).isTrue();
        circuit.stop();
        System.out.println(circuit.getLastRunDuration());
        assertThat(circuit.getLastRunDuration()).isGreaterThan(1_000_000L);
    }
}
