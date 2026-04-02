package com.orion.engineering.core.polling;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PollingTaskService
{
    private final ScheduledExecutorService scheduler;
    private final Map<UUID, ScheduledTaskHandle<?>> tasks = new ConcurrentHashMap<>();


    public PollingTaskService(int schedulerPoolSize)
    {
        this.scheduler = Executors.newScheduledThreadPool(Math.max(1, schedulerPoolSize),
                        runnable -> {
                            Thread t = new Thread(runnable);
                            t.setName("polling-task-" + t.getId());
                            t.setDaemon(true);
                            return t;
                        });
    }


    public void shutdownGracefully(Duration wait) throws InterruptedException
    {
        scheduler.shutdown();
        if(!scheduler.awaitTermination(wait.toMillis(), TimeUnit.MILLISECONDS))
        {
            scheduler.shutdownNow();
        }
    }


    /**
     * Start a polling task.
     *
     * @param supplier      operation to call periodically (must be thread-safe for your use-case)
     * @param stopCondition returns true when supplier result means "stop and succeed"
     * @param interval      time between invocations (use Duration.ofMinutes(1) for your case)
     * @param timeout       optional timeout; if null or Duration.ZERO, no timeout
     * @param onSuccess     called once with the final value when stopCondition becomes true (may be null)
     * @param onError       called on error or timeout (may be null)
     * @param <T>           type returned by supplier
     * @return Pair of (taskId, CompletableFuture<T>) — the future completes when task stops successfully
     */
    public <T> PollingTask<T> startPolling(
                    Supplier<T> supplier,
                    Predicate<T> stopCondition,
                    Duration interval,
                    Duration timeout,
                    Consumer<T> onSuccess,
                    Consumer<Throwable> onError
    )
    {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(stopCondition);
        Objects.requireNonNull(interval);
        UUID taskId = UUID.randomUUID();
        CompletableFuture<T> completion = new CompletableFuture<>();
        AtomicBoolean finished = new AtomicBoolean(false);
        // avoid overlapping runs for this task
        AtomicBoolean running = new AtomicBoolean(false);
        // The periodic runnable
        Runnable poller = () -> {
            if(finished.get())
            {
                return; // already finished
            }
            // Prevent overlapping
            if(!running.compareAndSet(false, true))
            {
                // previous invocation still running — skip this tick
                return;
            }
            try
            {
                T result;
                try
                {
                    result = supplier.get();
                }
                catch(Throwable t)
                {
                    // supplier threw an exception -> mark as failed
                    if(finished.compareAndSet(false, true))
                    {
                        completion.completeExceptionally(t);
                        if(onError != null)
                        {
                            onError.accept(t);
                        }
                        cancelTaskInternal(taskId);
                    }
                    return;
                }
                boolean stop = false;
                try
                {
                    stop = stopCondition.test(result);
                }
                catch(Throwable t)
                {
                    if(finished.compareAndSet(false, true))
                    {
                        completion.completeExceptionally(t);
                        if(onError != null)
                        {
                            onError.accept(t);
                        }
                        cancelTaskInternal(taskId);
                    }
                    return;
                }
                if(stop && finished.compareAndSet(false, true))
                {
                    completion.complete(result);
                    if(onSuccess != null)
                    {
                        onSuccess.accept(result);
                    }
                    cancelTaskInternal(taskId);
                }
                // otherwise continue, wait for next scheduled execution
            }
            finally
            {
                running.set(false);
            }
        };
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(poller, 0L, interval.toMillis(), TimeUnit.MILLISECONDS);
        ScheduledFuture<?> timeoutFuture = null;
        if(timeout != null && !timeout.isZero())
        {
            timeoutFuture = scheduler.schedule(() -> {
                if(finished.compareAndSet(false, true))
                {
                    TimeoutException tex = new TimeoutException("Polling task timed out after " + timeout);
                    completion.completeExceptionally(tex);
                    if(onError != null)
                    {
                        onError.accept(tex);
                    }
                    cancelTaskInternal(taskId);
                }
            }, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        ScheduledTaskHandle<T> handle = new ScheduledTaskHandle<>(taskId, scheduledFuture, timeoutFuture, completion, onSuccess, onError);
        tasks.put(taskId, handle);
        return new PollingTask<>(taskId, completion);
    }


    /**
     * Cancel a running task by id. Returns true if cancellation was performed.
     */
    public boolean cancelTask(UUID taskId)
    {
        ScheduledTaskHandle<?> h = tasks.remove(taskId);
        if(h == null)
        {
            return false;
        }
        // mark as cancelled and cancel scheduled futures
        h.cancel();
        return true;
    }


    private void cancelTaskInternal(UUID taskId)
    {
        ScheduledTaskHandle<?> h = tasks.remove(taskId);
        if(h != null)
        {
            h.cancel();
        }
    }


    private static final class ScheduledTaskHandle<T>
    {
        final UUID id;
        final ScheduledFuture<?> scheduledFuture;
        final ScheduledFuture<?> timeoutFuture;
        final CompletableFuture<T> completion;
        final Consumer<T> onSuccess;
        final Consumer<Throwable> onError;


        ScheduledTaskHandle(UUID id, ScheduledFuture<?> scheduledFuture,
                        ScheduledFuture<?> timeoutFuture,
                        CompletableFuture<T> completion,
                        Consumer<T> onSuccess, Consumer<Throwable> onError)
        {
            this.id = id;
            this.scheduledFuture = scheduledFuture;
            this.timeoutFuture = timeoutFuture;
            this.completion = completion;
            this.onSuccess = onSuccess;
            this.onError = onError;
        }


        void cancel()
        {
            try
            {
                scheduledFuture.cancel(true);
            }
            catch(Throwable ignored)
            {
            }
            if(timeoutFuture != null)
            {
                try
                {
                    timeoutFuture.cancel(true);
                }
                catch(Throwable ignored)
                {
                }
            }
            // if not completed, complete exceptionally due to cancellation
            if(!completion.isDone())
            {
                completion.completeExceptionally(new CancellationException("Polling task cancelled"));
                if(onError != null)
                {
                    onError.accept(new CancellationException("Polling task cancelled"));
                }
            }
        }
    }


    /**
     * Returned when a polling task is started.
     */
    public static final class PollingTask<T>
    {
        private final UUID id;
        private final CompletableFuture<T> future;


        public PollingTask(UUID id, CompletableFuture<T> future)
        {
            this.id = id;
            this.future = future;
        }


        public UUID getId()
        {
            return id;
        }


        public CompletableFuture<T> getFuture()
        {
            return future;
        }
    }
}
