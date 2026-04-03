package com.orion.enginering.electrical.circuit;

import static org.assertj.core.api.Assertions.assertThat;

import com.orion.engineering.electrical.Voltage;
import com.orion.engineering.electrical.circuit.Circuit;
import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.Battery;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Terminal;
import com.orion.engineering.electrical.circuit.component.Wire;
import com.orion.enginering.TestBase;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.junit.jupiter.api.Test;

public class CircuitTest extends TestBase
{
    @Test
    void simpleCircuit()
    {
        Battery battery = Battery.builder()
                        .name("battery")
                        .voltage(Voltage.builder()
                                        .value(1.0d)
                                        .build())
                        .positiveTerminal(new Terminal())
                        .negativeTerminal(new Terminal())
                        .build();
        Wire wire1 = Wire.builder()
                        .name("wire")
                        .length(1.0d)
                        .positiveTerminal(new Terminal())
                        .negativeTerminal(new Terminal())
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
        graph.addEdge(wire1, battery, TerminalToTerminalConnection.builder()
                        .fromTerminal(wire1.getPositiveTerminal())
                        .toTerminal(battery.getPositiveTerminal())
                        .build());
        Circuit circuit = Circuit.builder()
                        .graph(graph)
                        .build();
        assertThat(circuit.isThereVoltageSource()).isTrue();
        assertThat(circuit.isClosed()).isTrue();
    }
}
