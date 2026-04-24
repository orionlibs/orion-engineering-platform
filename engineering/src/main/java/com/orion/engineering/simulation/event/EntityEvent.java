package com.orion.engineering.simulation.event;

public record EntityEvent(long timestamp, String targetID, String type)
                implements TargetedEvent
{
    @Override
    public int priority()
    {
        return 0;
    }


    @Override
    public String getTargetID()
    {
        return targetID;
    }
}