package com.orion.engineering.physics.simulation;

import com.orion.engineering.math.geometry.vector.Vec2;

/** Immutable kinematic snapshot: position + velocity. */
public record PhysicsState(Vec2 position, Vec2 velocity)
{
    /**
     * Semi-implicit Euler — better energy conservation than explicit Euler,
     * correct behaviour for springs and oscillators.
     */
    public PhysicsState integrate(Vec2 acceleration, double dt)
    {
        Vec2 newVelocity = velocity.add(acceleration.scale(dt));
        Vec2 newPosition = position.add(newVelocity.scale(dt));
        return new PhysicsState(newPosition, newVelocity);
    }


    /** Kinetic energy: ½mv² */
    public double kineticEnergy(double mass)
    {
        return 0.5 * mass * velocity.magnitudeSquared();
    }
    // Convenience factories ---------------------------------------------------


    public static PhysicsState at(double x, double y)
    {
        return new PhysicsState(new Vec2(x, y), Vec2.ZERO);
    }


    public static PhysicsState at(double x, double y, double vx, double vy)
    {
        return new PhysicsState(new Vec2(x, y), new Vec2(vx, vy));
    }
}