package org.rpis5.chapters.chapter_07.mongo_rx_repo;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("Duplicates")
@EnableMongoRepositories
@SpringBootApplication
@RequiredArgsConstructor
public class Chapter7RxMongoApplication implements CommandLineRunner {
   private static final Logger log = LoggerFactory.getLogger(Chapter7RxMongoApplication.class);

   private final BookSpringDataMongoRxRepository bookRepo;
   private final RxMongoTemplateQueryService rxMongoTemplateQueryService;
   private final RxMongoDriverQueryService rxMongoDriverQueryService;

	public static void main(String[] args) {
		SpringApplication.run(Chapter7RxMongoApplication.class, args);
	}

	@Override
	public void run(String... args) {

		Flux<Book> books = Flux.just(
			new Book("The Martian", 2011, "Andy Weir"),
			new Book("Blue Mars", 1996, "Kim Stanley Robinson"),
			new Book("The War of the Worlds", 1898, "H. G. Wells"),
			new Book("Artemis", 2017, "Andy Weir"),
			new Book("The Expanse: Leviathan Wakes", 2011, "Daniel Abraham", "Ty Franck"),
			new Book("The Expanse: Caliban's War", 2012, "Daniel Abraham", "Ty Franck")
		);

		bookRepo
			.saveAll(books)
			.subscribe();

		log.info("Books saved in DB");

		Flux<Book> allBooks = bookRepo.findAll();
		log.info("All books in DB: \n{}", toString(allBooks).block());

		Flux<Book> andyWeirBooks = bookRepo.findByAuthorsOrderByPublishingYearDesc(Flux.just("Andy Weir"));
		log.info("All books by Andy Weir: \n{}", toString(andyWeirBooks).block());

		Flux<Book> booksWithFewAuthors = bookRepo.booksWithFewAuthors();
		log.info("Books with few authors: \n{}", toString(booksWithFewAuthors).block());

		log.info("--- Custom Query Service with ReactiveMongoTemplate -----------------------------");

		log.info("Search for books with Mars: \n{}",
			toString(rxMongoTemplateQueryService.findBooksByTitle("Expanse")).block());

      log.info("--- Custom Query Service with ReactiveStreams Mongo Driver ----------------------");
      log.info("Search for books with Mars: \n{}",
         toString(rxMongoDriverQueryService.findBooksByTitle("Expanse", true)).block());
	}

   private Mono<String> toString(Flux<Book> books) {
		return books.map(Book::toString)
			.reduce(new StringBuilder(), (sb, b) -> sb.append(" - ").append(b).append("\n"))
			.map(StringBuilder::toString);
	}
}
