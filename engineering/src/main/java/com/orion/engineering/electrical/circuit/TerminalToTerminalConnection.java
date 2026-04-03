package com.orion.engineering.electrical.circuit;

import com.orion.engineering.electrical.circuit.component.Terminal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jgrapht.graph.DefaultEdge;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class TerminalToTerminalConnection extends DefaultEdge
{
    private Terminal fromTerminal;
    private Terminal toTerminal;
}
