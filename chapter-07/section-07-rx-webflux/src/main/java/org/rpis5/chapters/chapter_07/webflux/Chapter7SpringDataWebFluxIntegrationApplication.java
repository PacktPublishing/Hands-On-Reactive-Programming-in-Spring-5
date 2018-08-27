package org.rpis5.chapters.chapter_07.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Chapter7SpringDataWebFluxIntegrationApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Chapter7SpringDataWebFluxIntegrationApplication.class, args);
	}

	@Override
	public void run(String... args) {

	}
}
