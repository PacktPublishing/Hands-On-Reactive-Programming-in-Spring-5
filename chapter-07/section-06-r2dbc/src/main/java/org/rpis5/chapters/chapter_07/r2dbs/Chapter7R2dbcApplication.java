package org.rpis5.chapters.chapter_07.r2dbs;

import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.function.TransactionalDatabaseClient;

import java.time.Duration;
import java.util.Arrays;

/**
 * ATTENTION: Test requires running Docker Engine!
 *
 * Application may fail on the first start-up due to timeout for Docker image download.
 * Please, retry a few times in case of failures.
 */
@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@Import({
    PostgresConfiguration.class,
    InfrastructureConfiguration.class
})
public class Chapter7R2dbcApplication implements CommandLineRunner {

    private final PostgresqlConnectionFactory pgConnectionFactory;
    private final TransactionalDatabaseClient databaseClient;
    private final BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(Chapter7R2dbcApplication.class, args);
    }

    @Override
    public void run(String... args) {
        databaseClient.execute()
            .sql("create table book (id integer, title varchar(50), publishing_year integer);")
            .fetch()
            .rowsUpdated()
            .doOnSuccess(count -> log.info("Database schema created"))
            .block();

        // Manual data insert
        databaseClient.execute()
            .sql("insert into book (id, title, publishing_year) values (4, 'Yellow Mars', 2009);")
            .fetch()
            .rowsUpdated()
            .doOnSuccess(count -> log.info("Manual book insert, inserted {}", count))
            .block();

        databaseClient.inTransaction(session ->
            session.execute()
                .sql("select * from book where id = 4")
                .as(Book.class)
                .fetch()
                .all())
            .subscribe(b -> log.info("Book from transaction: {}", b));

        // Note: Repo is in alpha, no ID is generated
        bookRepository.saveAll(Arrays.asList(
            new Book("Blue Mars", 2009),
            new Book("Red Mars", 1998),
            new Book("Pink Mars", 2009)
        ))
            .count()
            .doOnSuccess(count -> log.info("{} books inserted", count))
            .block(Duration.ofSeconds(2));

        bookRepository.findById(1)
            .doOnSuccess(b -> log.info("Book with id=1: {}", b))
            .block();

        bookRepository.findAll()
            .doOnNext(book -> log.info("Book: {}", book))
            .count()
            .doOnSuccess(count -> log.info("Database contains {} books", count))
            .block();

        log.info("The latest books in the DB:");
        bookRepository.findTheLatestBooks()
            .doOnNext(book -> log.info("Book: {}", book))
            .count()
            .doOnSuccess(count -> log.info("Database contains {} latest books", count))
            .block();

        log.info("Using raw R2DBC Client");
        // Using raw client
        R2dbc r2dbc = new R2dbc(pgConnectionFactory);

        r2dbc.inTransaction(handle ->
            handle
                .execute("insert into book (id, title, publishing_year) " +
                        "values ($1, $2, $3)",
                    20, "The Sands of Mars", 1951)
                .doOnNext(inserted -> log.info("{} rows was inserted into DB", inserted))
        ).thenMany(r2dbc.inTransaction(handle ->
            handle.select("SELECT title FROM book")
                .mapResult(result ->
                    result.map((row, rowMetadata) ->
                        row.get("title", String.class)))))
            .doOnNext(elem -> log.info(" - Title: {}", elem))
            .blockLast();

        log.info("Application finished successfully!");
    }

}
