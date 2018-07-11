package org.rpis5.chapters.chapter_07.jpa;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
@RequiredArgsConstructor
public class Chapter7JpaApplication implements CommandLineRunner {
   private static final Logger log = LoggerFactory.getLogger(Chapter7JpaApplication.class);

   private final BookSpringDataJpaRepository bookRepository;

	public static void main(String[] args) {
		SpringApplication.run(Chapter7JpaApplication.class, args);
	}

	@Override
	public void run(String... args) {
      log.info("All books in DB: \n{}", toString(bookRepository.findAll()));

      log.info("All books with ids in range 12..15: \n{}", toString(bookRepository.findByIdBetween(12, 15)));

      log.info("The book with the shortest titles: \n{}", toString(bookRepository.findShortestTitle()));
	}

   private String toString(Iterable<Book> books) {
	   StringBuilder sb = new StringBuilder();
      books.iterator().forEachRemaining(b ->
         sb.append(" - ").append(b.toString()).append("\n"));
      return sb.toString();
   }
}
