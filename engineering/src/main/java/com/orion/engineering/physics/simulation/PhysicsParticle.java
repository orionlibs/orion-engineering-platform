package com.orion.engineering.physics.simulation;

import com.orion.engineering.math.geometry.vector.Vec2;
import com.orion.engineering.simulation.core.SimulationEntity;
import com.orion.engineering.simulation.event.SimulationEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Point-mass entity that integrates F = ma on every {@link PhysicsTickEvent}.
 *
 * <pre>{@code
 * PhysicsParticle ball = PhysicsParticle.builder("ball")
 *     .position(0, 0)
 *     .velocity(10, 20)
 *     .mass(1.0)
 *     .withForce(Forces.gravity(9.81))
 *     .recordHistory()
 *     .build();
 * }</pre>
 */
public class PhysicsParticle implements SimulationEntity
{
    private final String id;
    private final double mass;
    private final List<ForceProvider> forces;
    private final boolean recordHistory;
    private volatile PhysicsState state;
    private final List<PhysicsState> history; // populated only when recordHistory = true


    private PhysicsParticle(Builder b)
    {
        this.id = b.id;
        this.mass = b.mass;
        this.forces = List.copyOf(b.forces);
        this.recordHistory = b.recordHistory;
        this.state = b.initialState;
        this.history = b.recordHistory ? new ArrayList<>() : Collections.emptyList();
        if(b.recordHistory)
        {
            history.add(state);
        }
    }


    @Override
    public String getID()
    {
        return id;
    }


    @Override
    public void onEvent(SimulationEvent event)
    {
        if(!(event instanceof PhysicsTickEvent pte))
        {
            return;
        }
        Vec2 totalForce = forces.stream()
                        .map(f -> f.compute(state, pte.simulationTime(), mass))
                        .reduce(Vec2.ZERO, Vec2::add);

        state = state.integrate(totalForce.scale(1.0 / mass), pte.dt());
        if(recordHistory)
        {
            (history).add(state);
        }
    }
    // Accessors ---------------------------------------------------------------


    public Vec2 getPosition()
    {
        return state.position();
    }


    public Vec2 getVelocity()
    {
        return state.velocity();
    }


    public PhysicsState getState()
    {
        return state;
    }


    public double getMass()
    {
        return mass;
    }


    public double kineticEnergy()
    {
        return state.kineticEnergy(mass);
    }


    /** Full trajectory. Populated only when {@code recordHistory()} was set. */
    public List<PhysicsState> getHistory()
    {
        return Collections.unmodifiableList(history);
    }
    // Builder -----------------------------------------------------------------


    public static Builder builder(String id)
    {
        return new Builder(id);
    }


    public static final class Builder
    {
        private final String id;
        private double mass = 1.0;
        private PhysicsState initialState = PhysicsState.at(0, 0);
        private final List<ForceProvider> forces = new ArrayList<>();
        private boolean recordHistory = false;


        private Builder(String id)
        {
            this.id = id;
        }


        public Builder mass(double m)
        {
            this.mass = m;
            return this;
        }


        public Builder state(PhysicsState s)
        {
            this.initialState = s;
            return this;
        }


        public Builder recordHistory()
        {
            this.recordHistory = true;
            return this;
        }


        public Builder position(double x, double y)
        {
            this.initialState = new PhysicsState(new Vec2(x, y), initialState.velocity());
            return this;
        }


        public Builder velocity(double vx, double vy)
        {
            this.initialState = new PhysicsState(initialState.position(), new Vec2(vx, vy));
            return this;
        }


        public Builder withForce(ForceProvider f)
        {
            forces.add(f);
            return this;
        }


        public PhysicsParticle build()
        {
            if(mass <= 0)
            {
                throw new IllegalArgumentException("Mass must be > 0, got: " + mass);
            }
            return new PhysicsParticle(this);
        }
    }
}