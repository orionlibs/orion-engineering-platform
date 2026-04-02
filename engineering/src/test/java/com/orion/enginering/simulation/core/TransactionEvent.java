package com.orion.enginering.simulation.core;

import com.orion.engineering.simulation.event.TargetedEvent;

public record TransactionEvent(long timestamp, String entityId, double amount) implements TargetedEvent
{
    @Override
    public String getTargetID()
    {
        return entityId;
    }


    @Override
    public int priority()
    {
        return 1;
    }
}