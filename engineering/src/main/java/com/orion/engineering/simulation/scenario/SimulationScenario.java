package com.orion.engineering.simulation.scenario;

import com.orion.engineering.math.geometry.vector.Vec2;
import com.orion.engineering.physics.simulation.ForceProvider;
import com.orion.engineering.physics.simulation.Forces;
import com.orion.engineering.physics.simulation.PhysicsParticle;
import com.orion.engineering.physics.simulation.PhysicsState;
import com.orion.engineering.physics.simulation.PhysicsTickEvent;
import com.orion.engineering.simulation.core.GenericSimulationEngine;
import com.orion.engineering.simulation.core.SimulationCommand;
import com.orion.engineering.simulation.event.SystemEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Fluent entry-point for building, running, and querying classical-mechanics
 * simulations. Wraps the engine completely — no latches, no anonymous entities,
 * no manual event scheduling needed in tests.
 *
 * <pre>{@code
 * var s = SimulationScenario.create()
 *     .gravity(9.81)
 *     .timeStep(0.01)
 *     .particle("ball")
 *         .position(0, 0).velocity(10, 20).mass(1.0)
 *         .add()
 *     .runForSeconds(2.0);
 *
 * assertEquals(20.0, s.positionOf("ball").x(), 0.1);
 * }</pre>
 */
public final class SimulationScenario
{
    private final Map<String, PhysicsParticle> particles;


    private SimulationScenario(Map<String, PhysicsParticle> particles)
    {
        this.particles = Map.copyOf(particles);
    }
    // ── Result query API ──────────────────────────────────────────────────────


    public PhysicsParticle particle(String id)
    {
        PhysicsParticle p = particles.get(id);
        if(p == null)
        {
            throw new NoSuchElementException("No particle with id: " + id);
        }
        return p;
    }


    public Vec2 positionOf(String id)
    {
        return particle(id).getPosition();
    }


    public Vec2 velocityOf(String id)
    {
        return particle(id).getVelocity();
    }


    /** Full trajectory — populated only when {@code recordHistory()} was set on the particle. */
    public List<PhysicsState> historyOf(String id)
    {
        return particle(id).getHistory();
    }


    public Collection<PhysicsParticle> allParticles()
    {
        return particles.values();
    }
    // ── Entry-point ───────────────────────────────────────────────────────────


    public static ScenarioBuilder create()
    {
        return new ScenarioBuilder();
    }
    // =========================================================================
    //  ScenarioBuilder
    // =========================================================================


    public static final class ScenarioBuilder
    {
        private double dt = 0.01;
        private final List<ForceProvider> globalForces = new ArrayList<>();
        private final Map<String, PhysicsParticle.Builder> particleBuilders = new LinkedHashMap<>();


        private ScenarioBuilder()
        {
        }


        /** Apply uniform downward gravity to every particle. */
        public ScenarioBuilder gravity(double g)
        {
            return globalForce(Forces.gravity(g));
        }


        /** Apply any force to every particle (e.g. a global wind). */
        public ScenarioBuilder globalForce(ForceProvider force)
        {
            globalForces.add(force);
            return this;
        }


        /** Integration time-step in seconds (default: 0.01 s). */
        public ScenarioBuilder timeStep(double dt)
        {
            this.dt = dt;
            return this;
        }


        /** Begin configuring a particle; finish with {@link ParticleBuilder#add()}. */
        public ParticleBuilder particle(String id)
        {
            return new ParticleBuilder(this, id);
        }
        // ── Run methods ───────────────────────────────────────────────────────


        /** Schedule ticks and run for the requested simulation seconds. */
        public SimulationScenario runForSeconds(double seconds)
        {
            return runForTicks(Math.round(seconds / dt));
        }


        /** Schedule exactly {@code ticks} physics steps and run. */
        public SimulationScenario runForTicks(long ticks)
        {
            Map<String, PhysicsParticle> built = buildParticles();
            try(GenericSimulationEngine engine = new GenericSimulationEngine())
            {
                built.values().forEach(engine::registerEntity);
                for(long tick = 1; tick <= ticks; tick++)
                {
                    engine.scheduleEvent(new PhysicsTickEvent(tick, dt));
                }
                engine.scheduleEvent(new SystemEvent(ticks + 1, SimulationCommand.SHUTDOWN));
                engine.run(ticks + 1);
            }
            return new SimulationScenario(built);
        }
        // ── Internal ──────────────────────────────────────────────────────────


        private Map<String, PhysicsParticle> buildParticles()
        {
            Map<String, PhysicsParticle> built = new LinkedHashMap<>();
            for(var entry : particleBuilders.entrySet())
            {
                PhysicsParticle.Builder pb = entry.getValue();
                globalForces.forEach(pb::withForce);   // inject global forces
                built.put(entry.getKey(), pb.build());
            }
            return built;
        }
    }
    // =========================================================================
    //  ParticleBuilder — chains back to ScenarioBuilder via add()
    // =========================================================================


    public static final class ParticleBuilder
    {
        private final ScenarioBuilder parent;
        private final String id;
        private final PhysicsParticle.Builder delegate;


        private ParticleBuilder(ScenarioBuilder parent, String id)
        {
            this.parent = parent;
            this.id = id;
            this.delegate = PhysicsParticle.builder(id);
        }


        public ParticleBuilder position(double x, double y)
        {
            delegate.position(x, y);
            return this;
        }


        public ParticleBuilder velocity(double vx, double vy)
        {
            delegate.velocity(vx, vy);
            return this;
        }


        public ParticleBuilder mass(double mass)
        {
            delegate.mass(mass);
            return this;
        }


        public ParticleBuilder state(PhysicsState s)
        {
            delegate.state(s);
            return this;
        }


        public ParticleBuilder withForce(ForceProvider f)
        {
            delegate.withForce(f);
            return this;
        }


        public ParticleBuilder recordHistory()
        {
            delegate.recordHistory();
            return this;
        }


        /** Finish this particle and return to the scenario builder. */
        public ScenarioBuilder add()
        {
            parent.particleBuilders.put(id, delegate);
            return parent;
        }
    }
}