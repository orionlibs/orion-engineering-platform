package com.orion.enginering.simulation.core;

import com.orion.engineering.simulation.core.SimulationContext;
import com.orion.engineering.simulation.core.SimulationEntity;
import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.TickEvent;

public class Projectile implements SimulationEntity
{
    private final String id;
    private double positionY; // Meters
    private double velocityY; // Meters per second
    private final double gravity = -9.81;
    private final double deltaTime = 0.1; // 100ms steps


    public Projectile(String id, double initialHeight, double initialVelocity)
    {
        this.id = id;
        this.positionY = initialHeight;
        this.velocityY = initialVelocity;
    }


    @Override
    public String getID()
    {
        return id;
    }


    @Override
    public void onEvent(SimulationEvent event)
    {
        if(event instanceof TickEvent)
        {
            updatePhysics();
        }
    }


    private void updatePhysics()
    {
        if(positionY <= 0)
        {
            return; // Already hit the ground
        }
        // Euler Integration
        velocityY += gravity * deltaTime;
        positionY += velocityY * deltaTime;
        // Ensure we don't go below ground
        if(positionY < 0)
        {
            positionY = 0;
        }
        System.out.printf("[%d ms] %s: Height = %.2fm, Velocity = %.2fm/s%n", SimulationContext.CURRENT_TIME.get(), id, positionY, velocityY);
    }


    public double getHeight()
    {
        return positionY;
    }
}
