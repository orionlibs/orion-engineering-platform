package com.orion.engineering.simulation;

public class SimulationContext
{
    public static final ScopedValue<Long> CURRENT_TIME = ScopedValue.newInstance();
    public static final ScopedValue<String> SIMULATION_ID = ScopedValue.newInstance();
}
