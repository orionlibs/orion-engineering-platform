package com.orion.enginering.simulation.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.orion.engineering.simulation.core.GenericSimulationEngine;
import com.orion.engineering.simulation.event.TickEvent;
import com.orion.enginering.TestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProjectileSimulationEngineTest extends TestBase
{
    private GenericSimulationEngine engine;


    @BeforeEach
    void setUp()
    {
        engine = new GenericSimulationEngine();
    }


    @AfterEach
    void tearDown()
    {
        engine.close();
    }


    @Test
    @DisplayName("Projectile should hit the ground after 5 seconds")
    void testProjectileFalling()
    {
        // Arrange: Start at 50m, 0 velocity
        double initialHeight = 50.0;
        var ball = new Projectile("ball-1", initialHeight, 0.0);
        engine.registerEntity(ball);
        // Schedule ticks every 100ms for 5 seconds
        for(long t = 0; t <= 5000; t += 100)
        {
            engine.scheduleEvent(new TickEvent(t));
        }
        engine.run(5000);
        // Basic physics check: h = h0 + 0.5 * g * t^2
        // 50 + 0.5 * (-9.81) * (5^2) = 50 - 122.6 = -72.6m (so it should be 0)
        assertEquals(0.0, ball.getHeight(), 0.01, "Ball should have hit the ground (height 0)");
    }


    @Test
    @DisplayName("Projectile should be mid-air at 1 second")
    void testProjectileMidAir()
    {
        var ball = new Projectile("ball-2", 100.0, 0.0);
        engine.registerEntity(ball);
        for(long t = 0; t <= 1000; t += 100)
        {
            engine.scheduleEvent(new TickEvent(t));
        }
        engine.run(1000);
        // At 1s, h ≈ 100 - (0.5 * 9.81 * 1^2) ≈ 95.1m
        // Using Euler integration (which we are), it will be slightly different
        // but definitely less than 100 and greater than 90.
        assertTrue(ball.getHeight() < 100.0, "Ball should have fallen slightly");
        assertTrue(ball.getHeight() > 90.0, "Ball should still be high in the air");
    }
}
