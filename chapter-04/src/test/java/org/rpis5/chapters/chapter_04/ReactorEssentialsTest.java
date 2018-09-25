package org.rpis5.chapters.chapter_04;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;


@Slf4j
public class ReactorEssentialsTest {

    private final Random random = new Random();

    @Test
    @Ignore
    public void endlessStream() {
        Flux.interval(Duration.ofMillis(1))
            .collectList()
            .block();
    }

    @Test
    @Ignore
    public void endlessStream2() {
        Flux.range(1, 5)
            .repeat()
            .doOnNext(e -> log.info("E: {}", e))
            .take(100)
            .blockLast();
    }

    @Test
    @Ignore
    public void endlessStreamAndCauseAnError() {
        Flux.range(1, 100)
            .repeat()
            .collectList()
            .block();
    }

    @Test
    public void createFlux() {
        Flux<String> stream1 = Flux.just("Hello", "world");
        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> stream3 = Flux.range(1, 500);

        Flux<String> emptyStream = Flux.empty();
        Flux<String> streamWithError = Flux.error(new RuntimeException("Hi!"));
    }

    @Test
    public void createMono() {
        Mono<String> stream4 = Mono.just("One");
        Mono<String> stream5 = Mono.justOrEmpty(null);
        Mono<String> stream6 = Mono.justOrEmpty(Optional.empty());

        Mono<String> stream7 = Mono.fromCallable(() -> httpRequest());
        Mono<String> stream8 = Mono.fromCallable(this::httpRequest);

        StepVerifier.create(stream8)
            .expectErrorMessage("IO error")
            .verify();

        Mono<Void> noData = Mono.fromRunnable(() -> doLongAction());

        StepVerifier.create(noData)
            .expectSubscription()
            .expectNextCount(0)
            .expectComplete()
            .verify();
    }

    @Test
    public void emptyOrError() {
        Flux<String> empty = Flux.empty();
        Mono<String> error = Mono.error(new RuntimeException("Unknown id"));
    }

    @Test
    public void managingSubscription() throws InterruptedException {
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
            .doOnCancel(() -> log.info("Cancelled"))
            .subscribe(
                data -> log.info("onNext: {}", data)
            );
        Thread.sleep(200);
        disposable.dispose();
    }

    @Test
    public void subscribingOnStream() throws Exception {
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                log.info("initial request for 1 element");
                subscription.request(1);
            }

            public void onNext(String s) {
                log.info("onNext: {}", s);
                log.info("requesting 1 more element");
                subscription.request(1);
            }

            public void onComplete() {
                log.info("onComplete");
            }

            public void onError(Throwable t) {
            }
        };

        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);

        Thread.sleep(100);
    }

    @Test
    public void simpleSubscribe() {
        Flux.just("A", "B", "C")
            .subscribe(
                System.out::println,
                errorIgnored -> {
                },
                () -> System.out.println("Done"));
    }

    @Test
    public void mySubscriber() {
        Flux.just("A", "B", "C")
            .subscribe(new MySubscriber<>());
    }

    @Test
    public void simpleRange() {
        Flux.range(2010, 9)
            .subscribe(y -> System.out.print(y + ","));
    }

    @Test
    public void shouldCreateDefer() {
        Mono<User> userMono = requestUserData(null);
        StepVerifier.create(userMono)
            .expectNextCount(0)
            .expectErrorMessage("Invalid user id")
            .verify();
    }

    @Test
    public void startStopStreamProcessing() throws Exception {
        Mono<?> startCommand = Mono.delay(Duration.ofSeconds(1));
        Mono<?> stopCommand = Mono.delay(Duration.ofSeconds(3));
        Flux<Long> streamOfData = Flux.interval(Duration.ofMillis(100));

        streamOfData
            .skipUntilOther(startCommand)
            .takeUntilOther(stopCommand)
            .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    public void collectSort() {
        Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
            .collectSortedList(Comparator.reverseOrder())
            .subscribe(System.out::println);
    }

    @Test
    public void indexElements() {
        Flux.range(2018, 5)
            .timestamp()
            .index()
            .subscribe(e -> log.info("index: {}, ts: {}, value: {}",
                e.getT1(),
                Instant.ofEpochMilli(e.getT2().getT1()),
                e.getT2().getT2()));
    }

    @Test
    public void findingIfThereIsEvenElements() {
        Flux.just(3, 5, 7, 9, 11, 15, 16, 17)
            .any(e -> e % 2 == 0)
            .subscribe(hasEvens -> log.info("Has evens: {}", hasEvens));
    }

    @Test
    public void reduceExample() {
        Flux.range(1, 5)
            .scan(0, (acc, elem) -> acc + elem)
            .subscribe(result -> log.info("Result: {}", result));
    }

    @Test
    public void runningAverageExample() {
        int bucketSize = 5;
        Flux.range(1, 500)
            .index()
            .scan(
                new int[bucketSize],
                (acc, elem) -> {
                    acc[(int) (elem.getT1() % bucketSize)] = elem.getT2();
                    return acc;
                })
            .skip(bucketSize)
            .map(array -> Arrays.stream(array).sum() * 1.0 / bucketSize)
            .subscribe(av -> log.info("Running average: {}", av));
    }

    @Test
    public void thenOperator() {
        Flux.just(1, 2, 3)
            .thenMany(Flux.just(5, 6))
            .subscribe(e -> log.info("onNext: {}", e));
    }

    @Test
    public void combineLatestOperator() {
        Flux.concat(
            Flux.range(1, 3),
            Flux.range(4, 2),
            Flux.range(6, 5)
        ).subscribe(e -> log.info("onNext: {}", e));
    }

    @Test
    public void bufferBySize() {
        Flux.range(1, 13)
            .buffer(4)
            .subscribe(e -> log.info("onNext: {}", e));
    }

    @Test
    public void windowByPredicate() {
        Flux<Flux<Integer>> fluxFlux = Flux.range(101, 20)
            .windowUntil(this::isPrime, true);

        fluxFlux.subscribe(window -> window
            .collectList()
            .subscribe(e -> log.info("window: {}", e)));
    }

    @Test
    public void groupByExample() {
        Flux.range(1, 7)
            .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
            .subscribe(groupFlux -> groupFlux
                .scan(
                    new LinkedList<>(),
                    (list, elem) -> {
                        if (list.size() > 1) {
                            list.remove(0);
                        }
                        list.add(elem);
                        return list;
                    })
                .filter(arr -> !arr.isEmpty())
                .subscribe(data ->
                    log.info("{}: {}",
                        groupFlux.key(),
                        data)));
    }

    private Flux<String> requestBooks(String user) {
        return Flux.range(1, random.nextInt(3) + 1)
            .delayElements(Duration.ofMillis(3))
            .map(i -> "book-" + i);
    }

    @Test
    public void flatMapExample() throws InterruptedException {
        Flux.just("user-1", "user-2", "user-3")
            .flatMap(u -> requestBooks(u)
                .map(b -> u + "/" + b))
            .subscribe(r -> log.info("onNext: {}", r));

        Thread.sleep(1000);
    }

    @Test
    public void sampleExample() throws InterruptedException {
        Flux.range(1, 100)
            .delayElements(Duration.ofMillis(1))
            .sample(Duration.ofMillis(20))
            .subscribe(e -> log.info("onNext: {}", e));

        Thread.sleep(1000);
    }

    @Test
    public void doOnExample() {
        Flux.just(1, 2, 3)
            .concatWith(Flux.error(new RuntimeException("Conn error")))
            .doOnEach(s -> log.info("signal: {}", s))
            .subscribe();
    }

    @Test
    public void signalProcessing() {
        Flux.range(1, 3)
            .doOnNext(e -> System.out.println("data  : " + e))
            .materialize()
            .doOnNext(e -> System.out.println("signal: " + e))
            .dematerialize()
            .collectList()
            .subscribe(r -> System.out.println("result: " + r));
    }

    @Test
    public void signalProcessingWithLog() {
        Flux.range(1, 3)
            .log("FluxEvents")
            .subscribe(e -> {}, e -> {}, () -> {}, s -> s.request(2));
    }

    @Test
    public void tryWithResources() {
        try (Connection conn = Connection.newConnection()) {
            conn.getData().forEach(
                data -> log.info("Received data: {}", data)
            );
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }
    }

    @Test
    public void usingOperator() {
        Flux<String> ioRequestResults = Flux.using(
            Connection::newConnection,
            connection -> Flux.fromIterable(connection.getData()),
            Connection::close
        );

        ioRequestResults
            .subscribe(
                data -> log.info("Received data: {}", data),
                e -> log.info("Error: {}", e.getMessage()),
                () -> log.info("Stream finished"));
    }

    static class Transaction {
        private static final Random random = new Random();
        private final int id;

        public Transaction(int id) {
            this.id = id;
            log.info("[T: {}] created", id);
        }

        public static Mono<Transaction> beginTransaction() {
            return Mono.defer(() ->
                Mono.just(new Transaction(random.nextInt(1000))));
        }

        public Flux<String> insertRows(Publisher<String> rows) {
            return Flux.from(rows)
                .delayElements(Duration.ofMillis(100))
                .flatMap(row -> {
                    if (random.nextInt(10) < 2) {
                        return Mono.error(new RuntimeException("Error on: " + row));
                    } else {
                        return Mono.just(row);
                    }
                });
        }


        public Mono<Void> commit() {
            return Mono.defer(() -> {
                log.info("[T: {}] commit", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conflict"));
                }
            });
        }

        public Mono<Void> rollback() {
            return Mono.defer(() -> {
                log.info("[T: {}] rollback", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conn error"));
                }
            });
        }
    }

    @Test
    public void usingWhenExample() throws InterruptedException {
        Flux.usingWhen(
            Transaction.beginTransaction(),
            transaction -> transaction.insertRows(Flux.just("A", "B")),
            Transaction::commit,
            Transaction::rollback
        ).subscribe(
            d -> log.info("onNext: {}", d),
            e -> log.info("onError: {}", e.getMessage()),
            () -> log.info("onComplete")
        );

        Thread.sleep(1000);
    }

    @Test
    public void usingPushOperator() throws InterruptedException {
        Flux.push(emitter -> IntStream
            .range(2000, 100000)
            .forEach(emitter::next))
            .delayElements(Duration.ofMillis(1))
            .subscribe(e -> log.info("onNext: {}", e));

        Thread.sleep(1000);
    }

    @Test
    public void usingCreateOperator() throws InterruptedException {
        Flux.create(emitter -> {
            emitter.onDispose(() -> log.info("Disposed"));
            // push events to emitter
        })
            .subscribe(e -> log.info("onNext: {}", e));

        Thread.sleep(1000);
    }

    @Test
    public void usingGenerate() throws InterruptedException {
        Flux.generate(
            () -> Tuples.of(0L, 1L),
            (state, sink) -> {
                log.info("generated value: {}", state.getT2());
                sink.next(state.getT2());
                long newValue = state.getT1() + state.getT2();
                return Tuples.of(state.getT2(), newValue);
            })
            .take(7)
            .subscribe(e -> log.info("onNext: {}", e));

        Thread.sleep(100);
    }

    @Test
    public void managingDemand() {
        Flux.range(1, 100)
            .subscribe(
                data -> log.info("onNext: {}", data),
                err -> { /* ignore */ },
                () -> log.info("onComplete"),
                subscription -> {
                    subscription.request(4);
                    subscription.cancel();
                }
            );
    }

    public Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {
            if (random.nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Conn error"))
                    .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue Mars", "The Expanse")
                    .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(s -> log.info("Request for {}", userId));
    }

    @Test
    public void handlingErrors() throws InterruptedException {
        Flux.just("user-1")
            .flatMap(user ->
                recommendedBooks(user)
                    .retryBackoff(5, Duration.ofMillis(100))
                    .timeout(Duration.ofSeconds(3))
                    .onErrorResume(e -> Flux.just("The Martian"))
            )
            .subscribe(
                b -> log.info("onNext: {}", b),
                e -> log.warn("onError: {}", e.getMessage()),
                () -> log.info("onComplete")
            );

        Thread.sleep(5000);
    }

    @Test
    public void coldPublisher() {
        Flux<String> coldPublisher = Flux.defer(() -> {
            log.info("Generating new items");
            return Flux.just(UUID.randomUUID().toString());
        });

        log.info("No data was generated so far");
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        log.info("Data was generated twice for two subscribers");
    }

    @Test
    public void connectExample() {
        Flux<Integer> source = Flux.range(0, 3)
            .doOnSubscribe(s ->
                log.info("new subscription for the cold publisher"));

        ConnectableFlux<Integer> conn = source.publish();

        conn.subscribe(e -> log.info("[Subscriber 1] onNext: {}", e));
        conn.subscribe(e -> log.info("[Subscriber 2] onNext: {}", e));

        log.info("all subscribers are ready, connecting");
        conn.connect();
    }

    @Test
    public void cachingExample() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 2)
            .doOnSubscribe(s ->
                log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));

        cachedSource.subscribe(e -> log.info("[S 1] onNext: {}", e));
        cachedSource.subscribe(e -> log.info("[S 2] onNext: {}", e));

        Thread.sleep(1200);

        cachedSource.subscribe(e -> log.info("[S 3] onNext: {}", e));
    }

    @Test
    public void replayExample() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
            .delayElements(Duration.ofMillis(100))
            .doOnSubscribe(s ->
                log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();

        cachedSource.subscribe(e -> log.info("[S 1] onNext: {}", e));
        Thread.sleep(400);
        cachedSource.subscribe(e -> log.info("[S 2] onNext: {}", e));

        Thread.sleep(1000);
    }

    @Test
    public void elapsedExample() throws InterruptedException {
        Flux.range(0, 5)
            .delayElements(Duration.ofMillis(100))
            .elapsed()
            .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));

        Thread.sleep(1000);
    }

    @Test
    public void transformExample() {
        Function<Flux<String>, Flux<String>> logUserInfo =
            stream -> stream
                .index()
                .doOnNext(tp ->
                    log.info("[{}] User: {}", tp.getT1(), tp.getT2()))
                .map(Tuple2::getT2);

        Flux.range(1000, 3)
            .map(i -> "user-" + i)
            .transform(logUserInfo)
            .subscribe(e -> log.info("onNext: {}", e));
    }

    @Test
    public void composeExample() {
        Function<Flux<String>, Flux<String>> logUserInfo = (stream) -> {
            if (random.nextBoolean()) {
                return stream
                    .doOnNext(e -> log.info("[path A] User: {}", e));
            } else {
                return stream
                    .doOnNext(e -> log.info("[path B] User: {}", e));
            }
        };

        Flux<String> publisher = Flux.just("1", "2")
            .compose(logUserInfo);

        publisher.subscribe();
        publisher.subscribe();
    }

    public Mono<User> requestUserData(String userId) {
        return Mono.defer(() ->
            isValid(userId)
                ? Mono.fromCallable(() -> requestUser(userId))
                : Mono.error(new IllegalArgumentException("Invalid user id")));
    }

    public Mono<User> requestUserData2(String userId) {
        return isValid(userId)
            ? Mono.fromCallable(() -> requestUser(userId))
            : Mono.error(new IllegalArgumentException("Invalid user id"));
    }

    private boolean isValid(String userId) {
        return userId != null;
    }

    private void doLongAction() {
        log.info("Long action");
    }

    private User requestUser(String id) {
        return new User();
    }

    private String httpRequest() {
        log.info("Making HTTP request");
        throw new RuntimeException("IO error");
    }

    public boolean isPrime(int number) {
        return number > 2
            && IntStream.rangeClosed(2, (int) Math.sqrt(number))
            .noneMatch(n -> (number % n == 0));
    }

    static class Connection implements AutoCloseable {
        private final Random rnd = new Random();

        static Connection newConnection() {
            log.info("IO Connection created");
            return new Connection();
        }

        public Iterable<String> getData() {
            if (rnd.nextInt(10) < 3) {
                throw new RuntimeException("Communication error");
            }
            return Arrays.asList("Some", "data");
        }

        @Override
        public void close() {
            log.info("IO Connection closed");
        }
    }

    public static class MySubscriber<T> extends BaseSubscriber<T> {

        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed");
            request(1);
        }

        public void hookOnNext(T value) {
            System.out.println(value);
            request(1);
        }
    }

    static class User {
        public String id, name;
    }
}
