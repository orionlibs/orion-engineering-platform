package com.orion.engineering.simulation.core;

import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.SystemEvent;
import com.orion.engineering.simulation.event.TargetedEvent;
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
        while(running && !eventQueue.isEmpty())
        {
            SimulationEvent nextEvent = eventQueue.peek();
            if(nextEvent.timestamp() > duration)
            {
                break;
            }
            long batchTime = nextEvent.timestamp();
            currentTime = batchTime;
            // create a NEW scope for this specific timestamp/batch
            try(var scope = StructuredTaskScope.open(Joiner.awaitAll(),
                            config -> config.withThreadFactory(Thread.ofVirtual().factory())))
            {
                while(!eventQueue.isEmpty() && eventQueue.peek().timestamp() == batchTime)
                {
                    SimulationEvent event = eventQueue.poll();
                    processEvent(event, scope, currentTime);
                }
                // Join this specific batch's scope
                scope.join();
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Simulation interrupted", e);
            }
            // Check if a SHUTDOWN event was processed in the last batch
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
            // The Engine routes ANY targeted event using the interface method
            case TargetedEvent te ->
            {
                SimulationEntity entity = entities.get(te.getTargetID());
                if(entity != null)
                {
                    scope.fork(() -> ScopedValue.where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                    .call(() -> {
                                        entity.onEvent(te);
                                        return null;
                                    }));
                }
            }
            case TickEvent t ->
            {
                for(SimulationEntity entity : entities.values())
                {
                    scope.fork(() -> ScopedValue.where(SimulationContext.CURRENT_TIME, timeAtEvent)
                                    .call(() -> {
                                        entity.onEvent(t);
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
            // No more IllegalStateException for new domain events!
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }


    // Helper method to keep things DRY (Don't Repeat Yourself)
    private void handleTargetedEvent(String id, SimulationEvent e, StructuredTaskScope<Object, Void> scope, long time)
    {
        SimulationEntity entity = entities.get(id);
        if(entity != null)
        {
            scope.fork(() -> ScopedValue.where(SimulationContext.CURRENT_TIME, time)
                            .call(() -> {
                                entity.onEvent(e);
                                return null;
                            }));
        }
    }


    @Override
    public void close()
    {
        // No longer need to manually manage an ExecutorService
    }
}
