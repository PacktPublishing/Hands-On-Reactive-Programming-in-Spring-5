package org.rpis5.chapters.chapter_07.mongo_repo;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@EnableMongoRepositories
@SpringBootApplication
@RequiredArgsConstructor
public class Chapter7RxMongoApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Chapter7RxMongoApplication.class);

    private final BookSpringDataMongoRepository bookRepo;

    public static void main(String[] args) {
        SpringApplication.run(Chapter7RxMongoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        bookRepo.save(new Book("The Martian", 2011, "Andy Weir"));
        bookRepo.save(new Book("Blue Mars", 1996, "Kim Stanley Robinson"));
        bookRepo.save(new Book("The War of the Worlds", 1898, "H. G. Wells"));
        bookRepo.save(new Book("Artemis", 2017, "Andy Weir"));
        bookRepo.save(new Book("The Expanse: Leviathan Wakes", 2011, "Daniel Abraham", "Ty Franck"));
        bookRepo.save(new Book("The Expanse: Caliban's War", 2012, "Daniel Abraham", "Ty Franck"));

        log.info("Books saved in DB");

        List<Book> allBooks = bookRepo.findAll();
        log.info("All books in DB: \n{}", toString(allBooks));

        Iterable<Book> andyWeirBooks = bookRepo.findByAuthorsOrderByPublishingYearDesc("Andy Weir");
        log.info("All books by Andy Weir: \n{}", toString(andyWeirBooks));

        Iterable<Book> booksWithFewAuthors = bookRepo.booksWithFewAuthors();
        log.info("Books with few authors: \n{}", toString(booksWithFewAuthors));

        log.info("Application finished successfully!");
    }

    private String toString(Iterable<Book> books) {
        StringBuilder sb = new StringBuilder();
        books.iterator().forEachRemaining(b ->
            sb.append(" - ").append(b.toString()).append("\n"));
        return sb.toString();
    }
}
