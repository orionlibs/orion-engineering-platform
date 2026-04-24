package com.orion.engineering.simulation.core;

import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.SystemEvent;
import com.orion.engineering.simulation.event.TargetedEvent;
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
        this.eventQueue = new PriorityQueue<>(
                        Comparator.comparingLong(SimulationEvent::timestamp)
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


    public long getCurrentTime()
    {
        return currentTime;
    }


    public void run(long duration)
    {
        running = true;
        while(running && !eventQueue.isEmpty())
        {
            SimulationEvent next = eventQueue.peek();
            if(next.timestamp() > duration)
            {
                break;
            }
            long batchTime = next.timestamp();
            currentTime = batchTime;
            try(var scope = StructuredTaskScope.open(
                            Joiner.awaitAll(),
                            cfg -> cfg.withThreadFactory(Thread.ofVirtual().factory())))
            {
                while(!eventQueue.isEmpty()
                                && eventQueue.peek().timestamp() == batchTime)
                {
                    processEvent(eventQueue.poll(), scope, currentTime);
                }
                scope.join();
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Simulation interrupted", e);
            }
            if(!running)
            {
                break;
            }
        }
    }


    private void processEvent(SimulationEvent event, StructuredTaskScope<Object, Void> scope, long timeAtEvent)
    {
        switch(event)
        {
            // All broadcast subtypes (TickEvent, PhysicsTickEvent, …) handled here.
            // New broadcast event types cost zero engine changes.
            case BroadcastEvent b ->
            {
                for(SimulationEntity entity : entities.values())
                {
                    scope.fork(() -> ScopedValue
                                    .where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                    .call(() -> {
                                        entity.onEvent(b);
                                        return null;
                                    }));
                }
            }
            case TargetedEvent te ->
            {
                SimulationEntity entity = entities.get(te.getTargetID());
                if(entity != null)
                {
                    scope.fork(() -> ScopedValue
                                    .where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                    .call(() -> {
                                        entity.onEvent(te);
                                        return null;
                                    }));
                }
            }
            case SystemEvent s ->
            {
                if(SimulationCommand.SHUTDOWN == s.command())
                {
                    running = false;
                }
            }
            default ->
            { /* Extensibility point — unknown event types are silently ignored */ }
        }
    }


    @Override
    public void close()
    {
    }
}