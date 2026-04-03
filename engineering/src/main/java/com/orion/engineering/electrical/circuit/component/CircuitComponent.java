package com.orion.engineering.electrical.circuit.component;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class CircuitComponent
{
    protected String name;
}
