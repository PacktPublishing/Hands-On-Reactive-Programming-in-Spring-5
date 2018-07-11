package org.rpis5.chapters.chapter_10;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rpis5.chapters.chapter_10.scheduler.CustomSchedulersFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@EnableAdminServer
@SpringBootApplication
public class CloudReadyApplication {
	private final MeterRegistry meterRegistry;

	public static void main(String[] args) {
		SpringApplication.run(CloudReadyApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Hooks.onNextDropped(c -> meterRegistry.counter("reactor.dropped.events").increment());
		Schedulers.setFactory(new CustomSchedulersFactory(meterRegistry));
		log.info("Updated Scheduler factory with custom instance");
	}
}
