package org.rpis5.chapters.chapter_05.reactive_app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Simple reactive application that simulates IoT sensor.
 *
 * Generates IoT data, saves data to database (MongoDB), and reports the recent measurements via SSE.
 * All operations are reactive (SSE, MongoDB write, MongoDB read).
 *
 * Using embedded MongoDB.
 *
 * Entry point: http://localhost:8080
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Chapter5ReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(Chapter5ReactiveApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> routerFunction(
		SensorReadingRepository sensorReadingRepository
	) {
		return RouterFunctions
			.route(
				GET("/"),
				serverRequest -> ServerResponse
					.ok()
					.contentType(MediaType.APPLICATION_STREAM_JSON)
					.body(sensorReadingRepository.findBy(), SensorsReadings.class));
	}

}
