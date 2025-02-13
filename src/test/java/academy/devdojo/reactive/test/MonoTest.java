package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

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

        log.info("---------------");

        /* StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();*/
    }
}
