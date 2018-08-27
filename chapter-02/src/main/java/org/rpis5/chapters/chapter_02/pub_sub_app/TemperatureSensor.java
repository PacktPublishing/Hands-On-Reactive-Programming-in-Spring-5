package org.rpis5.chapters.chapter_02.pub_sub_app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class TemperatureSensor {
   private final ApplicationEventPublisher publisher;
   private final Random rnd = new Random();
   private final ScheduledExecutorService executor =
           Executors.newSingleThreadScheduledExecutor();

   public TemperatureSensor(ApplicationEventPublisher publisher) {
      this.publisher = publisher;
   }

   @PostConstruct
   public void startProcessing() {
      this.executor.schedule(this::probe, 1, SECONDS);
   }

   private void probe() {
      double temperature = 16 + rnd.nextGaussian() * 10;
      publisher.publishEvent(new Temperature(temperature));

      // schedule the next read after some random delay (0-5 seconds)
      executor.schedule(this::probe, rnd.nextInt(5000), MILLISECONDS);
   }
}
