package com.orion.engineering.simulation.event;

public interface TargetedEvent extends SimulationEvent
{
    String getTargetID();
}
