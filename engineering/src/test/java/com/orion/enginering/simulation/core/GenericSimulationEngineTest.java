package com.orion.enginering.simulation.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.orion.engineering.simulation.core.GenericSimulationEngine;
import com.orion.engineering.simulation.core.SimulationCommand;
import com.orion.engineering.simulation.core.SimulationContext;
import com.orion.engineering.simulation.core.SimulationEntity;
import com.orion.engineering.simulation.event.EntityEvent;
import com.orion.engineering.simulation.event.SimulationEvent;
import com.orion.engineering.simulation.event.SystemEvent;
import com.orion.engineering.simulation.event.TickEvent;
import com.orion.enginering.TestBase;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GenericSimulationEngineTest extends TestBase
{
    private GenericSimulationEngine engine;


    @BeforeEach void setUp()
    {
        engine = new GenericSimulationEngine();
    }


    @AfterEach void tearDown()
    {
        engine.close();
    }
    // ── Helper ─────────────────────────────────────────────────────────────
    // Avoids repeating anonymous-class boilerplate across every test.


    private static SimulationEntity entity(String id, Consumer<SimulationEvent> handler)
    {
        return new SimulationEntity()
        {
            @Override
            public String getID()
            {
                return id;
            }


            @Override
            public void onEvent(SimulationEvent ev)
            {
                handler.accept(ev);
            }
        };
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("run() on an empty queue returns immediately without error")
    void testRunOnEmptyQueueReturnsImmediately()
    {
        assertDoesNotThrow(() -> engine.run(1000));
    }


    @Test
    @DisplayName("engine is usable inside try-with-resources without error")
    void testAutoCloseable()
    {
        assertDoesNotThrow(() -> {
            try(GenericSimulationEngine e = new GenericSimulationEngine())
            {
                e.scheduleEvent(new TickEvent(1));
                e.run(10);
            }
        });
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Ordering
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("events are dispatched in ascending timestamp order regardless of schedule order")
    void testChronologicalOrder()
    {
        List<Long> observed = new CopyOnWriteArrayList<>();
        engine.registerEntity(entity("e", ev -> observed.add(SimulationContext.CURRENT_TIME.get())));
        // Deliberately scheduled out of order
        engine.scheduleEvent(new EntityEvent(300, "e", "C"));
        engine.scheduleEvent(new EntityEvent(100, "e", "A"));
        engine.scheduleEvent(new EntityEvent(200, "e", "B"));
        engine.run(1000);
        assertEquals(List.of(100L, 200L, 300L), observed);
    }


    @Test
    @DisplayName("CURRENT_TIME advances monotonically across sequential batches")
    void testCurrentTimeAdvancesMonotonically()
    {
        List<Long> observed = new CopyOnWriteArrayList<>();
        engine.registerEntity(entity("clock", ev -> observed.add(SimulationContext.CURRENT_TIME.get())));
        long[] timestamps = {10, 20, 30, 40, 50};
        for(long t : timestamps)
        {
            engine.scheduleEvent(new TickEvent(t));
        }
        engine.run(100);
        assertEquals(Arrays.stream(timestamps).boxed().toList(), observed);
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Duration limit
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("events with timestamp > duration are never dispatched")
    void testDurationCutoffExcludesLateEvents()
    {
        AtomicInteger count = new AtomicInteger();
        engine.registerEntity(entity("e", ev -> count.incrementAndGet()));
        engine.scheduleEvent(new EntityEvent(50, "e", "WITHIN"));
        engine.scheduleEvent(new EntityEvent(200, "e", "BEYOND"));
        engine.run(100);
        assertEquals(1, count.get());
    }


    @Test
    @DisplayName("event at exactly the duration boundary is still dispatched")
    void testEventAtExactDurationBoundaryIsDispatched()
    {
        AtomicInteger count = new AtomicInteger();
        engine.registerEntity(entity("e", ev -> count.incrementAndGet()));
        engine.scheduleEvent(new EntityEvent(100, "e", "ON_BOUNDARY"));
        engine.run(100); // duration == timestamp: 100 > 100 is false, so it must fire
        assertEquals(1, count.get());
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Batching
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("all entities notified by a BroadcastEvent at time T observe CURRENT_TIME == T")
    void testAllEntitiesInBatchObserveSameTimestamp()
    {
        Set<Long> observedTimes = Collections.synchronizedSet(new LinkedHashSet<>());
        for(int i = 0; i < 8; i++)
        {
            engine.registerEntity(entity("e" + i,
                            ev -> observedTimes.add(SimulationContext.CURRENT_TIME.get())));
        }
        engine.scheduleEvent(new TickEvent(42));
        engine.run(100);
        // All 8 entities received the same tick — they must all see time = 42
        assertEquals(Set.of(42L), observedTimes);
    }


    @Test
    @DisplayName("events at different timestamps form separate batches, not one merged batch")
    void testSeparateTimestampsFormSeparateBatches()
    {
        // If batches leaked into each other, an entity for t=10 would observe t=20.
        Map<Long, Set<Long>> timesObservedAtBatch = new ConcurrentHashMap<>();
        for(long t : new long[] {10, 20})
        {
            engine.registerEntity(entity("e-at-" + t, ev -> {
                long batchTime = SimulationContext.CURRENT_TIME.get();
                timesObservedAtBatch.computeIfAbsent(t, k -> Collections.synchronizedSet(new HashSet<>()))
                                .add(batchTime);
            }));
            engine.scheduleEvent(new EntityEvent(t, "e-at-" + t, "PING"));
        }
        engine.run(100);
        assertEquals(Set.of(10L), timesObservedAtBatch.get(10L));
        assertEquals(Set.of(20L), timesObservedAtBatch.get(20L));
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  SHUTDOWN
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("SHUTDOWN stops the engine before the next batch, not mid-batch")
    void testShutdownCompletesCurrentBatchThenStops()
    {
        // SHUTDOWN has Integer.MIN_VALUE priority, so it is dequeued first within
        // the t=100 batch and sets running=false. The TickEvent at t=100 is still
        // in the same batch and must be forked and completed before the loop exits.
        // The TickEvent at t=200 must never be reached.
        AtomicInteger count = new AtomicInteger();
        engine.registerEntity(entity("e", ev -> count.incrementAndGet()));
        engine.scheduleEvent(new SystemEvent(100, SimulationCommand.SHUTDOWN));
        engine.scheduleEvent(new TickEvent(100));   // same batch as SHUTDOWN — must fire
        engine.scheduleEvent(new TickEvent(200));   // next batch              — must NOT fire
        engine.run(1000);
        assertEquals(1, count.get());
    }


    @Test
    @DisplayName("run() returns cleanly after SHUTDOWN even when many events remain queued")
    void testRunReturnsCleanlyAfterShutdown()
    {
        for(int i = 1; i <= 20; i++)
        {
            engine.scheduleEvent(new TickEvent(i * 100));
        }
        engine.scheduleEvent(new SystemEvent(300, SimulationCommand.SHUTDOWN));
        assertDoesNotThrow(() -> engine.run(Long.MAX_VALUE));
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Routing — BroadcastEvent
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("BroadcastEvent reaches every registered entity exactly once")
    void testBroadcastEventReachesAllEntities()
    {
        int n = 6;
        AtomicInteger count = new AtomicInteger();
        for(int i = 0; i < n; i++)
        {
            engine.registerEntity(entity("e" + i, ev -> count.incrementAndGet()));
        }
        engine.scheduleEvent(new TickEvent(10));
        engine.run(100);
        assertEquals(n, count.get());
    }


    @Test
    @DisplayName("multiple BroadcastEvents each reach all entities independently")
    void testMultipleBroadcastEventsEachReachAllEntities()
    {
        int entityCount = 4;
        int broadcastCount = 3;
        AtomicInteger total = new AtomicInteger();
        for(int i = 0; i < entityCount; i++)
        {
            engine.registerEntity(entity("e" + i, ev -> total.incrementAndGet()));
        }
        for(int t = 1; t <= broadcastCount; t++)
        {
            engine.scheduleEvent(new TickEvent(t * 10));
        }
        engine.run(1000);
        assertEquals(entityCount * broadcastCount, total.get());
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Routing — TargetedEvent
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("TargetedEvent is delivered only to the named entity")
    void testTargetedEventReachesOnlyTarget()
    {
        AtomicBoolean targetHit = new AtomicBoolean(false);
        AtomicBoolean bystanderHit = new AtomicBoolean(false);
        engine.registerEntity(entity("target", ev -> targetHit.set(true)));
        engine.registerEntity(entity("bystander", ev -> bystanderHit.set(true)));
        engine.scheduleEvent(new EntityEvent(10, "target", "PING"));
        engine.run(100);
        assertTrue(targetHit.get(), "Target must receive the event");
        assertFalse(bystanderHit.get(), "Bystander must not receive a targeted event");
    }


    @Test
    @DisplayName("TargetedEvent with an unknown entity ID is silently ignored")
    void testTargetedEventToUnknownIdIsIgnored()
    {
        engine.scheduleEvent(new EntityEvent(10, "ghost", "MSG"));
        assertDoesNotThrow(() -> engine.run(100));
    }


    @Test
    @DisplayName("BroadcastEvent and TargetedEvent co-existing in the same batch are both delivered correctly")
    void testMixedBroadcastAndTargetedInSameBatch()
    {
        AtomicInteger broadcastCount = new AtomicInteger();
        AtomicInteger targetCount = new AtomicInteger();
        // Entity "a" will receive one broadcast + one targeted = 2 events total.
        // Entity "b" will receive one broadcast only = 1 event total.
        engine.registerEntity(entity("a", ev -> {
            if(ev instanceof TickEvent)
            {
                broadcastCount.incrementAndGet();
            }
            if(ev instanceof EntityEvent)
            {
                targetCount.incrementAndGet();
            }
        }));
        engine.registerEntity(entity("b", ev -> {
            if(ev instanceof TickEvent)
            {
                broadcastCount.incrementAndGet();
            }
        }));
        engine.scheduleEvent(new TickEvent(10));               // → both entities
        engine.scheduleEvent(new EntityEvent(10, "a", "HIT")); // → only "a"
        engine.run(100);
        assertEquals(2, broadcastCount.get());
        assertEquals(1, targetCount.get());
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  ScopedValue context
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("CURRENT_TIME is bound inside onEvent and equals the event's timestamp")
    void testScopedValueCurrentTimeIsBoundAndCorrect()
    {
        AtomicBoolean wasBound = new AtomicBoolean(false);
        AtomicLong captured = new AtomicLong(-1);
        engine.registerEntity(entity("e", ev -> {
            wasBound.set(SimulationContext.CURRENT_TIME.isBound());
            captured.set(SimulationContext.CURRENT_TIME.get());
        }));
        engine.scheduleEvent(new EntityEvent(555, "e", "CHECK"));
        engine.run(1000);
        assertTrue(wasBound.get(), "CURRENT_TIME must be bound inside onEvent");
        assertEquals(555L, captured.get());
    }


    @Test
    @DisplayName("CURRENT_TIME is not bound on the calling thread after run() returns (no leakage)")
    void testScopedValueNotLeakedToCallingThread()
    {
        engine.scheduleEvent(new TickEvent(10));
        engine.run(100);
        // The ScopedValue binding is scoped to the virtual thread; it must
        // never bleed back to the thread that called run().
        assertFalse(SimulationContext.CURRENT_TIME.isBound());
    }
    // ═══════════════════════════════════════════════════════════════════════
    //  Virtual thread concurrency
    // ═══════════════════════════════════════════════════════════════════════


    @Test
    @DisplayName("hundreds of blocking tasks in one batch complete concurrently via virtual threads")
    void testVirtualThreadConcurrentExecution() throws InterruptedException
    {
        int n = 300;
        CountDownLatch latch = new CountDownLatch(n);
        for(int i = 0; i < n; i++)
        {
            engine.registerEntity(entity("w" + i, ev -> {
                try
                {
                    Thread.sleep(20);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
                latch.countDown();
            }));
        }
        // One broadcast tick triggers all 300 entities in a single batch.
        engine.scheduleEvent(new TickEvent(1));
        long start = System.currentTimeMillis();
        engine.run(10);
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(completed, "All virtual threads must complete");
        // Sequential would take 300 × 20 ms = 6 000 ms.
        // Concurrent virtual threads should finish well under 1 000 ms.
        assertTrue(elapsed < 1_500,
                        "Virtual threads must execute concurrently; elapsed = %d ms".formatted(elapsed));
    }
}
