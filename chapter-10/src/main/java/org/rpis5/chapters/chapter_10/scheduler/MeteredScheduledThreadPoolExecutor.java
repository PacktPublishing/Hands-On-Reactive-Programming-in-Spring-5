package org.rpis5.chapters.chapter_10.scheduler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Metrics friendly {@link ScheduledThreadPoolExecutor} extension.
 */
public class MeteredScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
   private final String poolName;
   private final MeterRegistry meterRegistry;
   private final ThreadLocal<Instant> taskExecutionTimer = new ThreadLocal<>();

   public MeteredScheduledThreadPoolExecutor(
      String poolName,
      int corePoolSize,
      MeterRegistry meterRegistry
   ) {
      super(corePoolSize);
      this.poolName = poolName;
      this.meterRegistry = meterRegistry;
      registerGauges();
   }

   @Override
   protected void beforeExecute(Thread thread, Runnable task) {
      super.beforeExecute(thread, task);
      taskExecutionTimer.set(Instant.now());
   }

   @Override
   protected void afterExecute(Runnable task, Throwable throwable) {
      Instant start = taskExecutionTimer.get();
      Timer timer = meterRegistry.timer(meterName("task.time"));
      timer.record(Duration.between(start, Instant.now()));

      super.afterExecute(task, throwable);
      if (throwable == null && task instanceof Future<?> && ((Future<?>) task).isDone()) {
         try {
            ((Future<?>) task).get();
         } catch (CancellationException ce) {
            throwable = ce;
         } catch (ExecutionException ee) {
            throwable = ee.getCause();
         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
         }
      }
      if (throwable != null) {
         Counter failedTasksCounter = meterRegistry.counter(meterName("failed.tasks"));
         failedTasksCounter.increment();
      } else {
         Counter successfulTasksCounter = meterRegistry.counter(meterName("successful.tasks"));
         successfulTasksCounter.increment();
      }
   }

   private void registerGauges() {
      meterRegistry.gauge(meterName("size"), this.getCorePoolSize());
      meterRegistry.gauge(meterName("active"), this.getActiveCount());
      meterRegistry.gauge(meterName("queue.size"), getQueue().size());
   }

   private String meterName(String s) {
      return "pool.scheduled." + poolName + "." + s;
   }
}
