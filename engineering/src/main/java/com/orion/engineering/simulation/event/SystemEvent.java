package com.orion.engineering.simulation.event;

public record SystemEvent(long timestamp, String command) implements SimulationEvent
{
    @Override
    public int priority()
    {
        return 2;
    }
}
