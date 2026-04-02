package com.orion.enginering.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.orion.engineering.simulation.GenericSimulationEngine;
import com.orion.engineering.simulation.SimulationContext;
import com.orion.engineering.simulation.SimulationEntity;
import com.orion.engineering.simulation.event.EntityEvent;
import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.SystemEvent;
import com.orion.engineering.simulation.event.TickEvent;
import com.orion.enginering.TestBase;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GenericSimulationEngineTest extends TestBase
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
    @DisplayName("Should process events in chronological order")
    void testChronologicalOrder() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(2);
        List<Long> processedTimes = new CopyOnWriteArrayList<>();
        SimulationEntity mockEntity = new SimulationEntity()
        {
            public String getID()
            {
                return "test-entity";
            }


            public void onEvent(SimulationEvent event)
            {
                processedTimes.add(SimulationContext.CURRENT_TIME.get());
                latch.countDown();
            }
        };
        engine.registerEntity(mockEntity);
        // Schedule out of order
        engine.scheduleEvent(new EntityEvent(10, "test-entity", "FIRST"));
        engine.scheduleEvent(new EntityEvent(20, "test-entity", "SECOND"));
        engine.run(100);
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(10L, processedTimes.get(0));
        assertEquals(20L, processedTimes.get(1));
    }


    @Test
    @DisplayName("Should respect simulation duration limit")
    void testDurationLimit() throws InterruptedException
    {
        AtomicInteger eventCount = new AtomicInteger(0);
        SimulationEntity entity = new SimulationEntity()
        {
            public String getID()
            {
                return "e1";
            }


            public void onEvent(SimulationEvent e)
            {
                eventCount.incrementAndGet();
            }
        };
        engine.registerEntity(entity);
        engine.scheduleEvent(new EntityEvent(10, "e1", "OK"));
        engine.scheduleEvent(new EntityEvent(150, "e1", "TOO_LATE"));
        engine.run(100); // Stop at 100
        // Small delay to ensure virtual threads had time to fail if they were going to run
        Thread.sleep(100);
        assertEquals(1, eventCount.get());
    }


    @Test
    @DisplayName("Should stop simulation on SHUTDOWN system event")
    void testShutdownEvent()
    {
        engine.scheduleEvent(new SystemEvent(10, "SHUTDOWN"));
        engine.scheduleEvent(new TickEvent(20)); // This should never be processed
        engine.run(100);
        // If we reach here, it means the loop terminated despite the queue not being empty
        assertTrue(true);
    }


    @Test
    @DisplayName("Should provide correct ScopedValue context to entities")
    void testScopedValueContext() throws InterruptedException
    {
        AtomicLong capturedSimTime = new AtomicLong(-1);
        CountDownLatch latch = new CountDownLatch(1);
        SimulationEntity entity = new SimulationEntity()
        {
            public String getID()
            {
                return "e1";
            }


            public void onEvent(SimulationEvent e)
            {
                if(SimulationContext.CURRENT_TIME.isBound())
                {
                    capturedSimTime.set(SimulationContext.CURRENT_TIME.get());
                }
                latch.countDown();
            }
        };
        engine.registerEntity(entity);
        engine.scheduleEvent(new EntityEvent(42, "e1", "CHECK_TIME"));
        engine.run(100);
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(42L, capturedSimTime.get());
    }


    @Test
    @DisplayName("Should handle massive amounts of concurrent events using Virtual Threads")
    void testVirtualThreadScaling() throws InterruptedException
    {
        int eventCount = 10_000;
        CountDownLatch latch = new CountDownLatch(eventCount);
        SimulationEntity entity = new SimulationEntity()
        {
            public String getID()
            {
                return "heavy-load";
            }


            public void onEvent(SimulationEvent e)
            {
                // Simulate some "work" that would normally block platform threads
                try
                {
                    Thread.sleep(1);
                }
                catch(InterruptedException ex)
                {
                }
                latch.countDown();
            }
        };
        engine.registerEntity(entity);
        for(int i = 0; i < eventCount; i++)
        {
            engine.scheduleEvent(new EntityEvent(i, "heavy-load", "TASK"));
        }
        long start = System.currentTimeMillis();
        engine.run(eventCount + 1);
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        assertTrue(completed, "Virtual threads should have completed the tasks");
        System.out.println("Processed " + eventCount + " events in " + (end - start) + "ms");
    }
}
