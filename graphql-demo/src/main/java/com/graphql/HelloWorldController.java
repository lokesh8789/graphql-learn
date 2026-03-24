package com.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class HelloWorldController {

    @QueryMapping
    public Mono<String> sayHello() {
        return Mono.just("Hello World")
                .delayElement(Duration.ofMillis(700));
    }

    @QueryMapping("printHello")
    public Mono<String> helloWorld() {
        return Mono.just("Printing hello World")
                .delayElement(Duration.ofMillis(1100));
    }

    @QueryMapping
    public Mono<String> sayHelloTo(@Argument String name) {
        return Mono.fromSupplier(() -> "Hello " + name)
                .delayElement(Duration.ofMillis(500));
    }

    @QueryMapping
    public Mono<Integer> random() {
        return Mono.fromSupplier(() -> ThreadLocalRandom.current().nextInt(1, 100));
    }
}
