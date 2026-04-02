package com.orion.engineering.simulation.event;

public record EntityEvent(long timestamp, String entityID, String action) implements SimulationEvent
{
    @Override
    public int priority()
    {
        return 1;
    }
}
