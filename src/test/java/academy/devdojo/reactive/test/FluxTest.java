package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
}
