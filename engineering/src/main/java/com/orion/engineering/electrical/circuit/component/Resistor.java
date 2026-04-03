package com.orion.engineering.electrical.circuit.component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public class Resistor extends CircuitComponent
{
    protected Terminal positiveTerminal;
    protected Terminal negativeTerminal;
    protected int resistance;


    public double getConductance()
    {
        return 1.0d / resistance;
    }
}
