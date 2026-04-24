package com.orion.engineering.physics.simulation;

import com.orion.engineering.math.geometry.vector.Vec2;

/** Factory methods for common classical-mechanics force providers. */
public final class Forces
{
    private Forces()
    {
    }


    /** Uniform downward acceleration (negative-Y). F = −g on the entity's mass. */
    public static ForceProvider gravity(double g)
    {
        return (state, t, mass) -> new Vec2(0.0, -mass * g);
    }


    /**
     * Hooke's-Law spring anchored at a fixed point.
     *
     * @param anchor     fixed attachment point
     * @param k          spring constant (N/m)
     * @param restLength natural length (m); use 0 for an ideal point spring
     */
    public static ForceProvider spring(Vec2 anchor, double k, double restLength)
    {
        return (state, t, m) -> {
            Vec2 disp = state.position().subtract(anchor);
            double dist = disp.magnitude();
            if(dist == 0.0)
            {
                return Vec2.ZERO;
            }
            return disp.normalize().scale(-k * (dist - restLength));
        };
    }


    /** Linear drag: F = −c·v  (viscous / low-Reynolds-number damping) */
    public static ForceProvider drag(double c)
    {
        return (state, t, m) -> state.velocity().scale(-c);
    }


    /** Quadratic drag: F = −c·|v|·v  (aerodynamic / high-speed drag) */
    public static ForceProvider quadraticDrag(double c)
    {
        return (state, t, m) -> {
            Vec2 v = state.velocity();
            return v.scale(-c * v.magnitude());
        };
    }


    /** Constant force in any direction (e.g. a steady wind or thrust). */
    public static ForceProvider constant(Vec2 force)
    {
        return (state, t, m) -> force;
    }


    /** Sinusoidal driving force in the Y-direction: F = A·sin(ωt). */
    public static ForceProvider sinusoidal(double amplitude, double omega)
    {
        return (state, t, m) -> new Vec2(0.0, amplitude * Math.sin(omega * t));
    }


    public static ForceProvider none()
    {
        return (state, t, m) -> Vec2.ZERO;
    }
}