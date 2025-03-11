package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
class MonoTest {

    @Test
    void monoSubscriber() {
        String name = "Carlos Daniel";
        Mono<String> mono = Mono.just(name).log();

        mono.subscribe();
        log.info("---------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumer() {
        String name = "Carlos Daniel";
        Mono<String> mono = Mono.just(name).log();

        mono.subscribe(s -> log.info("Value: '{}'", s));
        log.info("---------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerError() {
        String name = "Carlos Daniel";
        Mono<String> mono = Mono.just(name)
                .map(s -> {throw new RuntimeException("Testing error mono");});

        mono.subscribe(s -> log.info("Name: {}", s), s -> log.error(s.getMessage()));
        mono.subscribe(s -> log.info("Name: {}", s), Throwable::printStackTrace);
        log.info("---------------");
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void monoSubscriberConsumerComplete() {
        String name = "Carlos Daniel";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value: '{}'", s),
                Throwable::printStackTrace,
                () -> log.info("Finished"));

        log.info("---------------");

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerSubscription() {
        String name = "Carlos Daniel";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        mono.subscribe(s -> log.info("Value: '{}'", s),
                Throwable::printStackTrace,
                () -> log.info("Finished"),
                subscription -> subscription.request(5));

        log.info("---------------");

        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    void monoDoOnMethods() {
        String name = "Carlos Daniel";
        Mono<Object> mono = Mono.just(name)
                .log()
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request received. Starting doing something..."))
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .doOnSuccess(s -> log.info("doOnSuccess executed {}", s));

        mono.subscribe(s -> log.info("Value: '{}'", s),
                Throwable::printStackTrace,
                () -> log.info("Finished"));

    }

    @Test
    void monoDoOnError() {
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal Argument Exception Error"))
                .doOnError(e -> log.error(e.getMessage()))
                .doOnNext(s -> log.info("Executing doOnNext()"))
                .log();

        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void monoDoOnErrorResume() {
        String name = "Carlos Daniel";

        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal Argument Exception Error"))
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .onErrorResume(s -> {
                    log.info("Inside the On Error Resume");
                    return Mono.just(name);
                })
                .log();

        StepVerifier.create(error)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoDoOnErrorReturn() {
        String name = "Carlos Daniel";

        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal Argument Exception Error"))
                .onErrorReturn("empty")
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .onErrorResume(s -> {
                    log.info("Inside the On Error Resume");
                    return Mono.just(name);
                })
                .log();

        StepVerifier.create(error)
                .expectNext("empty")
                .verifyComplete();
    }
}
