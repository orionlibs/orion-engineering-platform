package com.orion.engineering.simulation.event;

import com.orion.computationalplatform.simulation.core.SimulationCommand;

public record SystemEvent(long timestamp, SimulationCommand command) implements SimulationEvent
{
    // Highest priority so SHUTDOWN is never delayed by concurrent batch events
    @Override
    public int priority()
    {
        return Integer.MIN_VALUE;
    }
}
