package com.orion.engineering.math.geometry.vector;

/** Immutable 2-D vector. Every operation returns a new instance. */
public record Vec2(double x, double y)
{
    public static final Vec2 ZERO = new Vec2(0.0, 0.0);


    public Vec2 add(Vec2 o)
    {
        return new Vec2(x + o.x, y + o.y);
    }


    public Vec2 subtract(Vec2 o)
    {
        return new Vec2(x - o.x, y - o.y);
    }


    public Vec2 scale(double s)
    {
        return new Vec2(x * s, y * s);
    }


    public Vec2 negate()
    {
        return new Vec2(-x, -y);
    }


    public double magnitude()
    {
        return Math.sqrt(x * x + y * y);
    }


    public double magnitudeSquared()
    {
        return x * x + y * y;
    }


    public double dot(Vec2 o)
    {
        return x * o.x + y * o.y;
    }


    public Vec2 normalize()
    {
        double m = magnitude();
        return m == 0.0 ? ZERO : scale(1.0 / m);
    }


    @Override
    public String toString()
    {
        return "Vec2(%.6f, %.6f)".formatted(x, y);
    }
}