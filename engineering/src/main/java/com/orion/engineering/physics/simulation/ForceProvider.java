package com.orion.engineering.physics.simulation;

import com.orion.engineering.math.geometry.vector.Vec2;

/**
 * A function from (state, simulationTimeSeconds) → force vector.
 * Compose with {@link #and} for multi-force particles.
 */
@FunctionalInterface
public interface ForceProvider
{
    Vec2 compute(PhysicsState state, double simulationTimeSeconds, double mass);


    default ForceProvider and(ForceProvider other)
    {
        return (s, t, m) -> this.compute(s, t, m).add(other.compute(s, t, m));
    }
}