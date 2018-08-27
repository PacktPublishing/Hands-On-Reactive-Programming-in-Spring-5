package org.rpis5.chapters.chapter_07.rxjava2jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootApplication
public class Chapter7JpaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Chapter7JpaApplication.class, args);
	}

	@Override
	public void run(String... args) {

	}

   private void reportResults(String message, Flux<Book> books) {
      books.map(Book::toString)
         .reduce(
            new StringBuffer(),
            (sb, b) -> sb.append(" - ")
               .append(b)
               .append("\n"))
         .doOnNext(sb -> log.info(message + "\n{}", sb))
         .subscribe();
   }
}
