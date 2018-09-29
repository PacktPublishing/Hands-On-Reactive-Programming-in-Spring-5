package org.rpis5.chapters.chapter_07.wrapped_sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@EnableJpaRepositories
@SpringBootApplication
@RequiredArgsConstructor
@Import({
    RxPersistenceConfiguration.class
})
public class Chapter7JpaApplication implements CommandLineRunner {
    private final RxBookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(Chapter7JpaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Flux<Book> books = Flux.just(
            new Book("The Martian", 2011),
            new Book("Blue Mars", 1996),
            new Book("The War of the Worlds", 1898),
            new Book("Artemis", 2016),
            new Book("The Expanse: Leviathan Wakes", 2011),
            new Book("The Expanse: Caliban's War", 2012)
        );

        bookRepository
            .saveAll(books)
            .count()
            .doOnNext(amount -> log.info("{} books saved in DB", amount))
            .block();

        Flux<Book> allBooks = bookRepository.findAll();
        reportResults("All books in DB:", allBooks);

        Flux<Book> andyWeirBooks = bookRepository
            .findByIdBetween(Mono.just(17), Mono.just(22));
        reportResults("Books with ids (17..22):", andyWeirBooks);

        Flux<Book> booksWithFewAuthors = bookRepository.findShortestTitle();
        reportResults("Books with the shortest title:", booksWithFewAuthors);

        Mono.delay(Duration.ofSeconds(5))
            .subscribe(e -> log.info("Application finished successfully!"));
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
