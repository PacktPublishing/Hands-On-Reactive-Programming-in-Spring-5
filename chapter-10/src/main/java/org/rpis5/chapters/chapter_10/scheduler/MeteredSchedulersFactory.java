package org.rpis5.chapters.chapter_10.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Custom Factory to create Reactor Schedulers.
 * Records operational metrics:
 *    - Number of executed tasks
 *    - Number of scheduled tasks
 *    - Number of tasks to schedule at a fixed rate
 */
@RequiredArgsConstructor
public class MeteredSchedulersFactory implements Schedulers.Factory {
   private final MeterRegistry meterRegistry;

   public ScheduledExecutorService decorateExecutorService(
      String schedulerType,
      Supplier<? extends ScheduledExecutorService> actual
   ) {
      ScheduledExecutorService actualScheduler = actual.get();
      String counterName = "scheduler." + schedulerType + ".execution";

      ScheduledExecutorService scheduledExecutorService = new ScheduledExecutorService() {
         @Override
         public void execute(Runnable command) {
            meterRegistry.counter(counterName, "type", "execute").increment();
            actualScheduler.execute(command);
         }

         @Override
         public void shutdown() {
            actualScheduler.shutdown();
         }

         @Override
         public List<Runnable> shutdownNow() {
            return actualScheduler.shutdownNow();
         }

         @Override
         public boolean isShutdown() {
            return actualScheduler.isShutdown();
         }

         @Override
         public boolean isTerminated() {
            return actualScheduler.isTerminated();
         }

         @Override
         public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return actualScheduler.awaitTermination(timeout, unit);
         }

         @Override
         public <T> Future<T> submit(Callable<T> task) {
            meterRegistry.counter(counterName, "type", "submit").increment();
            return actualScheduler.submit(task);
         }

         @Override
         public <T> Future<T> submit(Runnable task, T result) {
            meterRegistry.counter(counterName, "type", "submit").increment();
            return actualScheduler.submit(task, result);
         }

         @Override
         public Future<?> submit(Runnable task) {
            meterRegistry.counter(counterName, "type", "submit").increment();
            return actualScheduler.submit(task);
         }

         @Override
         public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            meterRegistry.counter(counterName, "type", "invokeAll").increment(tasks.size());
            return actualScheduler.invokeAll(tasks);
         }

         @Override
         public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            meterRegistry.counter(counterName, "type", "invokeAll").increment(tasks.size());
            return actualScheduler.invokeAll(tasks, timeout, unit);
         }

         @Override
         public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            meterRegistry.counter(counterName, "type", "invokeAny").increment(tasks.size());
            return actualScheduler.invokeAny(tasks);
         }

         @Override
         public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            meterRegistry.counter(counterName, "type", "invokeAny").increment(tasks.size());
            return actualScheduler.invokeAny(tasks, timeout, unit);
         }

         @Override
         public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            meterRegistry.counter(counterName, "type", "schedule").increment();
            return actualScheduler.schedule(command, delay, unit);
         }

         @Override
         public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            meterRegistry.counter(counterName, "type", "schedule").increment();
            return actualScheduler.schedule(callable, delay, unit);
         }

         @Override
         public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            meterRegistry.counter(counterName, "type", "scheduleAtFixedRate").increment();
            return actualScheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
         }

         @Override
         public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            meterRegistry.counter(counterName, "type", "scheduleAtFixedRate").increment();
            return actualScheduler.scheduleAtFixedRate(command, initialDelay, delay, unit);
         }
      };

      meterRegistry.counter("scheduler." + schedulerType + ".instances").increment();
      return scheduledExecutorService;
   }
}
