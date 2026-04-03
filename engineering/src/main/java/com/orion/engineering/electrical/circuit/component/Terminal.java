package com.orion.engineering.electrical.circuit.component;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Terminal
{
    private String name;
    private boolean isPositiveTerminal;
}
