package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
public class FluxTest {

    @Test
    void fluxSubscriber() {
        Flux<String> fluxString = Flux.just("Carlos", "Daniel", "Marchesin", "da", "Silva")
                .log();

        StepVerifier.create(fluxString)
                .expectNext("Carlos", "Daniel", "Marchesin", "da", "Silva")
                .verifyComplete();
    }

    @Test
    void fluxSubscriberIntegers() {
        Flux<Integer> fluxIntegers = Flux.range(1,5)
                .log();

        fluxIntegers.subscribe(i -> log.info("Number: '{}'", i));

        log.info("--------------------------");

        StepVerifier.create(fluxIntegers)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberFromList() {
        Flux<Integer> flux = Flux.fromIterable(List.of(1,2,3,4,5))
                .log();

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("-----------------------------------");
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberNumbersError() {
        Flux<Integer> flux = Flux.range(1,5)
                .log()
                .map(i -> {
                    if(i == 4) {
                        throw new IndexOutOfBoundsException("index error");
                    }
                    return i;
                });

        flux.subscribe(i -> log.info("Number {}", i), Throwable::printStackTrace,
                () -> log.info("DONE!"), subscription -> subscription.request(3));

        log.info("-----------------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

    @Test
    void fluxSubscriberNumbersUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1,10)
                .log();

        flux.subscribe(new Subscriber<>() {
            private int count = 0;
            private Subscription subscription;
            private int requestCount = 2;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if(count >= requestCount){
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("-----------------------------------");

        StepVerifier.create(flux)
                .expectNext(1, 2, 3,4,5,6,7,8,9,10)
                .verifyComplete();
    }
}
