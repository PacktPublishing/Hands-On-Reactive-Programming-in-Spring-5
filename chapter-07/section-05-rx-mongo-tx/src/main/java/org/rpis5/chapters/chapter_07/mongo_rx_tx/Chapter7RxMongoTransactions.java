package org.rpis5.chapters.chapter_07.mongo_rx_tx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Chapter7RxMongoTransactions implements CommandLineRunner {

	public static void main(String... args) {
		SpringApplication.run(Chapter7RxMongoTransactions.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("This application is verifiable through unit tests only!");
	}
}
