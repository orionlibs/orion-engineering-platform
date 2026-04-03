package com.orion.enginering.electrical.circuit.type;

import static org.assertj.core.api.Assertions.assertThat;

import com.orion.engineering.electrical.Voltage;
import com.orion.engineering.electrical.circuit.TerminalToTerminalConnection;
import com.orion.engineering.electrical.circuit.component.Battery;
import com.orion.engineering.electrical.circuit.component.CircuitComponent;
import com.orion.engineering.electrical.circuit.component.Lamp;
import com.orion.engineering.electrical.circuit.component.Terminal;
import com.orion.engineering.electrical.circuit.component.Wire;
import com.orion.engineering.electrical.circuit.type.DirectCurrentCircuit;
import com.orion.enginering.TestBase;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DirectCurrentCircuitTest extends TestBase
{
    Battery battery;


    @BeforeEach
    public void setup()
    {
        battery = Battery.builder()
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
    }


    @Test
    void simpleCircuitWithABatteryAndAWire()
    {
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
        assertThat(circuit.getLastRunDuration()).isGreaterThan(10_000L);
    }


    @Test
    void simpleCircuitWithABatteryAndALamp()
    {
        Wire wire1 = Wire.builder()
                        .name("wire1")
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
        Lamp lamp = Lamp.builder()
                        .name("lamp")
                        .resistance(100)
                        .positiveTerminal(Terminal.builder()
                                        .name("+")
                                        .isPositiveTerminal(true)
                                        .build())
                        .negativeTerminal(Terminal.builder()
                                        .name("-")
                                        .isPositiveTerminal(false)
                                        .build())
                        .build();
        Wire wire2 = Wire.builder()
                        .name("wire2")
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
        graph.addVertex(lamp);
        graph.addVertex(wire2);
        graph.addEdge(battery, wire1, TerminalToTerminalConnection.builder()
                        .fromTerminal(battery.getNegativeTerminal())
                        .toTerminal(wire1.getNegativeTerminal())
                        .build());
        graph.addEdge(wire1, lamp, TerminalToTerminalConnection.builder()
                        .fromTerminal(wire1.getPositiveTerminal())
                        .toTerminal(lamp.getPositiveTerminal())
                        .build());
        graph.addEdge(lamp, wire2, TerminalToTerminalConnection.builder()
                        .fromTerminal(lamp.getNegativeTerminal())
                        .toTerminal(wire2.getNegativeTerminal())
                        .build());
        TerminalToTerminalConnection c4 = TerminalToTerminalConnection.builder()
                        .fromTerminal(wire2.getPositiveTerminal())
                        .toTerminal(battery.getPositiveTerminal())
                        .build();
        graph.addEdge(wire2, battery, c4);
        DirectCurrentCircuit circuit = DirectCurrentCircuit.builder()
                        .graph(graph)
                        .build();
        circuit.printCircuit();
        circuit.start();
        assertThat(circuit.isThereVoltageSource()).isTrue();
        assertThat(circuit.isClosed()).isTrue();
        graph.removeEdge(c4);
        assertThat(circuit.isOpen()).isTrue();
        circuit.stop();
        assertThat(circuit.getLastRunDuration()).isGreaterThan(10_000L);
    }
}
