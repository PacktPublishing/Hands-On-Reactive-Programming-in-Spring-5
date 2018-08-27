package org.rpis5.chapters.chapter_10.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_10.service.Temperature;
import org.rpis5.chapters.chapter_10.service.TemperatureSensor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventStreamController {
   private final MeterRegistry meterRegistry;
   private final TemperatureSensor temperatureSensor;

   // Application monitoring
   private AtomicInteger activeStreams;

   @PostConstruct
   public void init() {
      activeStreams = meterRegistry.gauge("sse.streams", new AtomicInteger(0));
   }

   @GetMapping(path = "/temperature-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public Flux<Temperature> events() {
      return temperatureSensor.temperatureStream()
         .doOnSubscribe(subs -> activeStreams.incrementAndGet())
         .name("temperature.sse-stream")
         .metrics()
         .log("sse.temperature", Level.FINE)
         .doOnCancel(() -> activeStreams.decrementAndGet())
         .doOnTerminate(() -> activeStreams.decrementAndGet());
   }
}
