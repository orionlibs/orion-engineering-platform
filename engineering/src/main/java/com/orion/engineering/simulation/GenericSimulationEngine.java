package com.orion.engineering.simulation;

import com.orion.engineering.simulation.event.EntityEvent;
import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.SystemEvent;
import com.orion.engineering.simulation.event.TickEvent;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

public class GenericSimulationEngine implements AutoCloseable
{
    private final PriorityQueue<SimulationEvent> eventQueue;
    private final Map<String, SimulationEntity> entities;
    private long currentTime = 0;
    private volatile boolean running = false;


    public GenericSimulationEngine()
    {
        this.eventQueue = new PriorityQueue<>(Comparator.comparingLong(SimulationEvent::timestamp)
                        .thenComparingInt(SimulationEvent::priority));
        this.entities = new ConcurrentHashMap<>();
    }


    public void registerEntity(SimulationEntity entity)
    {
        entities.put(entity.getID(), entity);
    }


    public void scheduleEvent(SimulationEvent event)
    {
        eventQueue.add(event);
    }


    public void run(long duration)
    {
        running = true;
        // Open the scope once. It captures the initial empty ScopedValue state.
        try(var scope = StructuredTaskScope.open(Joiner.awaitAll(),
                        config -> config.withThreadFactory(Thread.ofVirtual().factory())))
        {
            while(running && !eventQueue.isEmpty())
            {
                SimulationEvent event = eventQueue.poll();
                if(event.timestamp() > duration)
                {
                    break;
                }
                currentTime = event.timestamp();
                processEvent(event, scope, currentTime);
            }
            // Wait for all spawned virtual threads to finish.
            scope.join();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulation interrupted", e);
        }
    }


    private void processEvent(SimulationEvent event, StructuredTaskScope<Object, Void> scope, long timeAtEvent)
    {
        switch(event)
        {
            case EntityEvent e ->
            {
                SimulationEntity entity = entities.get(e.entityID());
                if(entity != null)
                {
                    scope.fork(() -> {
                        return ScopedValue.where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                        .call(() -> {
                                            entity.onEvent(e);
                                            return null;
                                        });
                    });
                }
            }
            case TickEvent t ->
            {
                for(SimulationEntity entity : entities.values())
                {
                    scope.fork(() -> {
                        return ScopedValue.where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                        .call(() -> {
                                            entity.onEvent(t);
                                            return null;
                                        });
                    });
                }
            }
            case SystemEvent s ->
            {
                if(SimulationCommand.SHUTDOWN == s.command())
                {
                    running = false;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }


    @Override
    public void close()
    {
        // No longer need to manually manage an ExecutorService
    }
}
