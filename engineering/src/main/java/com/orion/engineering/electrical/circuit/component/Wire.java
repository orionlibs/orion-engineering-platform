package com.orion.engineering.electrical.circuit.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Wire implements CircuitComponent
{
    private double length;
    private Terminal positiveTerminal;
    private Terminal negativeTerminal;
    private String name;
}
