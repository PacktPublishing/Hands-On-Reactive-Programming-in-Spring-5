package org.rpis5.chapters.chapter_07.rx_dbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Chapter7ReactiveConnectorsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Chapter7ReactiveConnectorsApplication.class, args);
	}

	// TODO: Chat application with a limited history (1000 messages)?
	// Redis: active users (statuses for all & pub-sub)
	// MongoDB: messages in capped collection?
   // Couchbase: user profiles (nickname, country, email, picture)
   // Cassandra: audit log (message archive, status changes, online, offline, picture)
}
