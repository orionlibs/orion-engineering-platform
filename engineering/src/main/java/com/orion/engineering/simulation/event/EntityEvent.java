package com.orion.engineering.simulation.event;

public record EntityEvent(long timestamp, String entityID, String action) implements TargetedEvent
{
    @Override
    public String getTargetID()
    {
        return entityID; // Mapping the record field to the interface method
    }


    @Override
    public int priority()
    {
        return 1;
    }
}