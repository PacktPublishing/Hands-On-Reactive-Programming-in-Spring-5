package org.rpis5.chapters.chapter_07.rxjava2jdbc.book;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RxBookRepository {
   private static final String SAVE_QUERY =
         "insert into book (id, title, publishing_year) " +
         "values(:id, :title, :publishing_year) " +
         "on duplicate key " +
         "update title=:title, publishing_year=:publishing_year";

   private static final String SELECT_BY_ID =
      "select * from book where id=:id";

   private static final String SELECT_BY_TITLE =
      "select * from book where title=:title";

   private static final String SELECT_BY_YEAR_BETWEEN =
         "select * from book where " +
         "publishing_year >= :from and publishing_year <= :to";

   private final Database database;

   public Flowable<Book> save(Flowable<Book> books) {
      return books
         .flatMap(book -> save(book).toFlowable());
   }

   public Single<Book> save(Book book) {
      return database
         .update(SAVE_QUERY)
         .parameter("id", book.id())
         .parameter("title", book.title())
         .parameter("publishing_year", book.publishing_year())
         .counts()
         .ignoreElements()
         .andThen(Single.just(book));
   }

   public Flowable<Book> findAll() {
      return database
         .select(Book.class)
         .get();
   }

   public Maybe<Book> findById(String id) {
      return database
         .select(SELECT_BY_ID)
         .parameter("id", id)
         .autoMap(Book.class)
         .firstElement();
   }

   public Maybe<Book> findByTitle(Publisher<String> titlePublisher) {
      return Flowable.fromPublisher(titlePublisher)
         .firstElement()
         .flatMap(title -> database
            .select(SELECT_BY_TITLE)
            .parameter("title", title)
            .autoMap(Book.class)
            .firstElement());
   }

   public Flowable<Book> findByYearBetween(
      Single<Integer> from,
      Single<Integer> to
   ) {
      return Single
         .zip(from, to, Tuple2::new)
         .flatMapPublisher(tuple -> database
            .select(SELECT_BY_YEAR_BETWEEN)
            .parameter("from", tuple._1())
            .parameter("to", tuple._2())
            .autoMap(Book.class));
   }
}
