package com.orion.enginering.simulation.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.orion.engineering.math.geometry.vector.Vec2;
import com.orion.engineering.physics.simulation.Forces;
import com.orion.engineering.simulation.scenario.SimulationScenario;
import com.orion.enginering.TestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PhysicsSimulationEngineTest extends TestBase
{
    @Test
    @DisplayName("Projectile motion — parabolic trajectory")
    void testProjectileMotion()
    {
        double v0x = 10.0, v0y = 20.0, g = 9.81;
        double flightTime = 2.0 * v0y / g;        // ~4.077 s
        var s = SimulationScenario.create()
                        .gravity(g)
                        .timeStep(0.001)
                        .particle("ball")
                        .position(0, 0).velocity(v0x, v0y).mass(1.0)
                        .add()
                        .runForSeconds(flightTime);
        assertEquals(v0x * flightTime, s.positionOf("ball").x(), 0.1);
        assertEquals(0.0, s.positionOf("ball").y(), 0.5);
    }


    @Test
    @DisplayName("Spring-mass — half-period returns to −A (T = 2π√(m/k))")
    void testSpringOscillation()
    {
        double k = 4.0, m = 1.0;
        double halfPeriod = Math.PI * Math.sqrt(m / k);  // π/2 s
        var s = SimulationScenario.create()
                        .timeStep(0.0001)
                        .particle("bob")
                        .position(1.0, 0).velocity(0, 0).mass(m)
                        .withForce(Forces.spring(Vec2.ZERO, k, 0.0))
                        .add()
                        .runForSeconds(halfPeriod);
        assertEquals(-1.0, s.positionOf("bob").x(), 0.01);
    }


    @Test
    @DisplayName("Free fall — position matches s = h − ½gt²")
    void testFreeFall()
    {
        double g = 9.81, h = 100.0, t = 2.0;
        var s = SimulationScenario.create()
                        .gravity(g)
                        .timeStep(0.001)
                        .particle("stone")
                        .position(0, h).velocity(0, 0).mass(1.0)
                        .add()
                        .runForSeconds(t);
        assertEquals(h - 0.5 * g * t * t, s.positionOf("stone").y(), 0.1);
    }


    @Test
    @DisplayName("Damped oscillator — amplitude decays over time")
    void testDampedOscillator()
    {
        var s = SimulationScenario.create()
                        .timeStep(0.001)
                        .particle("mass")
                        .position(1.0, 0).velocity(0, 0).mass(1.0)
                        .withForce(Forces.spring(Vec2.ZERO, 4.0, 0.0))
                        .withForce(Forces.drag(0.4))
                        .recordHistory()
                        .add()
                        .runForSeconds(10.0);
        // Final amplitude must be less than initial displacement of 1 m
        assertTrue(Math.abs(s.positionOf("mass").x()) < 0.5);
    }


    @Test
    @DisplayName("Two independent particles feel global gravity equally")
    void testMultiParticleGravity()
    {
        SimulationScenario s = SimulationScenario.create()
                        .gravity(9.81)
                        .timeStep(0.01)
                        .particle("heavy").position(0, 100).mass(10.0).add()
                        .particle("light").position(0, 100).mass(0.1).add()
                        .runForSeconds(1.0);
        // Galileo: both fall the same distance
        assertEquals(s.positionOf("heavy").y(), s.positionOf("light").y(), 0.01);
    }
}
