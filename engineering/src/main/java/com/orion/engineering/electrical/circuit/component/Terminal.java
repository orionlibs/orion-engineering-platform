package com.orion.engineering.electrical.circuit.component;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Terminal
{
    private String name;
    private boolean isPositiveTerminal;
}
