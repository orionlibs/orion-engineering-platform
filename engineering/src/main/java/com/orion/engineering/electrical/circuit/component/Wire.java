package com.orion.engineering.electrical.circuit.component;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Wire extends CircuitComponent
{
    private double length;
    private Terminal positiveTerminal;
    private Terminal negativeTerminal;
}
