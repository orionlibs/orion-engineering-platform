package com.orion.engineering.electrical.circuit.component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode
public class Switch extends CircuitComponent
{
    protected Terminal positiveTerminal;
    protected Terminal negativeTerminal;
    protected boolean isOn;


    public void switchOn()
    {
        isOn = true;
    }


    public void switchOff()
    {
        isOn = false;
    }
}
