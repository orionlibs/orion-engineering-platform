package com.orion.engineering.electrical.circuit.component;

import com.orion.engineering.electrical.Voltage;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class VoltageSource extends CircuitComponent
{
    protected Voltage voltage;
    protected Terminal positiveTerminal;
    protected Terminal negativeTerminal;
}
