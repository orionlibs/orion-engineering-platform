package com.orion.engineering.simulation.event;

import com.orion.engineering.simulation.SimulationCommand;

public record SystemEvent(long timestamp, SimulationCommand command) implements SimulationEvent
{
    @Override
    public int priority()
    {
        return 2;
    }
}
