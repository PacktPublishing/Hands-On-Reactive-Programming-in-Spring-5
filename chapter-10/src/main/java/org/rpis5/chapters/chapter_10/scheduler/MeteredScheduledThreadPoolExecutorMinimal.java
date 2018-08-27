package org.rpis5.chapters.chapter_10.scheduler;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Metrics friendly {@link ScheduledThreadPoolExecutor} extension.
 */
@SuppressWarnings("unused")
public class MeteredScheduledThreadPoolExecutorMinimal extends ScheduledThreadPoolExecutor {

   public MeteredScheduledThreadPoolExecutorMinimal(
      int corePoolSize,
      MeterRegistry registry
   ) {
      super(corePoolSize);

      registry.gauge("pool.size", this.getCorePoolSize());
      registry.gauge("pool.active.tasks", this.getActiveCount());
      registry.gauge("pool.queue.size", getQueue().size());
   }
}
