package com.orion.engineering.electrical.circuit.component;

import com.orion.engineering.electrical.Voltage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class VoltageSource implements CircuitComponent
{
    protected Voltage voltage;
    protected Terminal positiveTerminal;
    protected Terminal negativeTerminal;
    private String name;
}
