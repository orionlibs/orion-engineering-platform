package com.orion.engineering.electrical.circuit.component;

import com.orion.engineering.electrical.Voltage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public class VoltageSource extends CircuitComponent
{
    protected Voltage voltage;
    protected Terminal positiveTerminal;
    protected Terminal negativeTerminal;
}
