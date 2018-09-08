package org.rpis5.chapters.chapter_07.wrapped_sync;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RequiredArgsConstructor
public abstract class
   ReactiveCrudRepositoryAdapter<T, ID, I extends CrudRepository<T, ID>>
   implements ReactiveCrudRepository<T, ID> {

   protected final I delegate;
   protected final Scheduler scheduler;

   @Override
   public <S extends T> Mono<S> save(S entity) {
      return Mono
         .fromCallable(() -> delegate.save(entity))
         .subscribeOn(scheduler);
   }

   @Override
   public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
      return Mono.fromCallable(() -> delegate.saveAll(entities))
         .flatMapMany(Flux::fromIterable)
         .subscribeOn(scheduler);
   }

   @Override
   public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
      return Flux.from(entityStream)
         .flatMap(entity -> Mono.fromCallable(() -> delegate.save(entity)))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<T> findById(ID id) {
      return Mono.fromCallable(() -> delegate.findById(id))
         .flatMap(result -> result
            .map(Mono::just)
            .orElseGet(Mono::empty))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<T> findById(Publisher<ID> id) {
      return Mono.from(id)
         .flatMap(actualId ->
            delegate.findById(actualId)
               .map(Mono::just)
               .orElseGet(Mono::empty))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Boolean> existsById(ID id) {
      return Mono
         .fromCallable(() -> delegate.existsById(id))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Boolean> existsById(Publisher<ID> id) {
      return Mono.from(id)
         .flatMap(actualId ->
            Mono.fromCallable(() -> delegate.existsById(actualId)))
         .subscribeOn(scheduler);
   }

   @Override
   public Flux<T> findAll() {
      return Mono
         .fromCallable(delegate::findAll)
         .flatMapMany(Flux::fromIterable)
         .subscribeOn(scheduler);
   }

   @Override
   public Flux<T> findAllById(Iterable<ID> ids) {
      return Mono
         .fromCallable(() -> delegate.findAllById(ids))
         .flatMapMany(Flux::fromIterable)
         .subscribeOn(scheduler);
   }

   @Override
   public Flux<T> findAllById(Publisher<ID> idStream) {
      return Flux
         .from(idStream)
         .buffer()
         .flatMap(ids -> Flux.fromIterable(delegate.findAllById(ids)))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Long> count() {
      return Mono
         .fromCallable(delegate::count)
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Void> deleteById(ID id) {
      return Mono
         .<Void>fromRunnable(() -> delegate.deleteById(id))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Void> deleteById(Publisher<ID> id) {
      return Mono.from(id)
         .flatMap(actualId ->
            Mono
               .<Void>fromRunnable(() -> delegate.deleteById(actualId))
               .subscribeOn(scheduler)
         );
   }

   @Override
   public Mono<Void> delete(T entity) {
      return Mono
         .<Void>fromRunnable(() -> delegate.delete(entity))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Void> deleteAll(Iterable<? extends T> entities) {
      return Mono
         .<Void>fromRunnable(() -> delegate.deleteAll(entities))
         .subscribeOn(scheduler);
   }

   @Override
   public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
      return Flux.from(entityStream)
         .flatMap(entity -> Mono
            .fromRunnable(() -> delegate.delete(entity))
            .subscribeOn(scheduler))
         .then();
   }

   @Override
   public Mono<Void> deleteAll() {
      return Mono
         .<Void>fromRunnable(delegate::deleteAll)
         .subscribeOn(scheduler);
   }
}
