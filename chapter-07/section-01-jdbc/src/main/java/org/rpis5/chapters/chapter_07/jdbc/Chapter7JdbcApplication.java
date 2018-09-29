package org.rpis5.chapters.chapter_07.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.extension.ExtensionCallback;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;


@EnableJdbcRepositories
@EnableAsync
@SpringBootApplication
@Import({
	JdbcConfiguration.class
})
public class Chapter7JdbcApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(Chapter7JdbcApplication.class);

	@Autowired
   DataSource dataSource;

	@Autowired
	BookJdbcRepository bookRepositoryJdbc;

	@Autowired
	BookSpringDataJdbcRepository bookRepositoryDataJdbc;

	public static void main(String[] args) {
		SpringApplication.run(Chapter7JdbcApplication.class, args);
	}

	@Override
	public void run(String... args) {

      // Spring JDBC
      log.info("--- Spring JDBC ---------------------------------------------");
      log.info("Book with id 13: {}", bookRepositoryJdbc.findById(13));

		List<Book> booksWithTitle = bookRepositoryJdbc.findByTitle("Blue Mars");
		log.info("Books with title 'Mars': {}", toString(booksWithTitle.stream()));

		List<Book> booksAll = bookRepositoryJdbc.findAll();
		log.info("All books in DB: {}", toString(booksAll.stream()));

		// Spring Data JDBC
      log.info("--- Spring Data JDBC------------------------------------------");
		Iterable<Book> booksFromDataJdbc = bookRepositoryDataJdbc.findAllById(asList(11, 13));
		List<Book> booksFromDataJdbcList = new ArrayList<>();
		booksFromDataJdbc.iterator().forEachRemaining(booksFromDataJdbcList::add);
		log.info("Books (id:11), (id:13) from Spring Data JDBC: {}",
			toString(booksFromDataJdbcList.stream()));

		log.info("Book with the longest title: {}",
			toString(bookRepositoryDataJdbc.findByLongestTitle().stream()));

		log.info("Book with the shortest title: {}",
			toString(bookRepositoryDataJdbc.findByShortestTitle()));

		bookRepositoryDataJdbc.findBookByTitleAsync("The Martian")
			.thenAccept(b -> log.info("Book by title, async: {}", toString(Stream.of(b))));

		bookRepositoryDataJdbc.findBooksByIdBetweenAsync(10, 14)
			.thenAccept(bookStream -> log.info("Book by id (11..13), async: {}", toString(bookStream)));

		// JDBI
      log.info("--- JDBI ----------------------------------------------------");
      Jdbi jdbi = Jdbi
         .create(dataSource)
         .installPlugin(new SqlObjectPlugin());

      List<Book> allBooksFromJdbi = jdbi.withExtension(BookJdbiDao.class, dao -> {
         dao.insertBook(new Book(20, "Moving Mars"));
         return dao.listBooks();
      });
      log.info("All Books according to Jdbi: {}", toString(allBooksFromJdbi.stream()));

      log.info("Application finished successfully!");
   }

	private String toString(Stream<Book> books) {
	   try {
         return books
            .map(Book::toString)
            .collect(joining("\n - ", "\n - ", ""));
      } finally {
	      // We have to close the stream when finished to free the resources used by the query.
	      books.close();
      }
	}
}
