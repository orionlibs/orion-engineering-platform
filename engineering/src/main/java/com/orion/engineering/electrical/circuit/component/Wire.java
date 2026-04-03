package com.orion.engineering.electrical.circuit.component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Wire extends CircuitComponent
{
    private double length;
    private Terminal positiveTerminal;
    private Terminal negativeTerminal;
}
