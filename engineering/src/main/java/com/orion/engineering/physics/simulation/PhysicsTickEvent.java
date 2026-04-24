package com.orion.engineering.physics.simulation;

import com.orion.engineering.simulation.core.BroadcastEvent;

/**
 * Broadcast tick that carries a real-world time-step so physics entities
 * can integrate equations of motion without knowing wall-clock time.
 */
public record PhysicsTickEvent(long timestamp, double dt) implements BroadcastEvent
{
    @Override
    public int priority()
    {
        return 0;
    }


    /** Simulation time in seconds: tick × dt */
    public double simulationTime()
    {
        return timestamp * dt;
    }
}
