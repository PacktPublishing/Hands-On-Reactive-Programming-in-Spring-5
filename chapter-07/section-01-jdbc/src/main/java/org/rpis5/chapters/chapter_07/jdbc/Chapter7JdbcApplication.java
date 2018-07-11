package org.rpis5.chapters.chapter_07.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@EnableJdbcRepositories
@SpringBootApplication
public class Chapter7JdbcApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(Chapter7JdbcApplication.class);

	@Autowired
	BookJdbcRepository bookRepositoryJdbc;

	@Autowired
	BookSpringDataJdbcRepository bookRepositoryDataJdbc;

	public static void main(String[] args) {
		SpringApplication.run(Chapter7JdbcApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("Book with id 13: {}", bookRepositoryJdbc.findById(13));

		List<Book> booksWithTitle = bookRepositoryJdbc.findByTitle("Blue Mars");
		log.info("Books with title 'Mars': {}", toString(booksWithTitle.stream()));

		List<Book> booksAll = bookRepositoryJdbc.findAll();
		log.info("All books in DB: {}", toString(booksAll.stream()));

		Iterable<Book> booksFromDataJdbc = bookRepositoryDataJdbc.findAllById(asList(11, 13));
		List<Book> booksFromDataJdbcList = new ArrayList<>();
		booksFromDataJdbc.iterator().forEachRemaining(booksFromDataJdbcList::add);
		log.info("Books (id:11), (id:13) from Spring Data JDBC: {}", toString(booksFromDataJdbcList.stream()));

		log.info("Book with the longest title: {}", bookRepositoryDataJdbc.findByLongestTitle());
	}

	private String toString(Stream<Book> books) {
		return books
			.map(Book::toString)
			.collect(joining("\n - ", "\n - ", ""));
	}
}
