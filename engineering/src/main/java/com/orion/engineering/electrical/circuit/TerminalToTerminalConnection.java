package com.orion.engineering.electrical.circuit;

import com.orion.engineering.electrical.circuit.component.Terminal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jgrapht.graph.DefaultEdge;

@Getter
@Builder
@AllArgsConstructor
public class TerminalToTerminalConnection extends DefaultEdge
{
    private Terminal fromTerminal;
    private Terminal toTerminal;
}
