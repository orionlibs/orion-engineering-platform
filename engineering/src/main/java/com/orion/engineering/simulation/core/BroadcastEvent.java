package com.orion.engineering.simulation.core;

import com.orion.engineering.simulation.event.SimulationEvent;

/**
 * Marker interface for events dispatched to every registered entity.
 * Adding new broadcast event types (e.g. PhysicsTickEvent) requires
 * zero changes to GenericSimulationEngine.
 */
public interface BroadcastEvent extends SimulationEvent
{
}
