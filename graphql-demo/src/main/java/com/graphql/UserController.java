package com.graphql;

import com.graphql.dto.Order;
import com.graphql.dto.User;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
public class UserController {

    private final Flux<User> userFlux = Flux.just(
            new User(1, "sam", 23, "London"),
            new User(2, "john", 21, "Berlin"),
            new User(3, "jake", 25, "Delhi"),
            new User(4, "mike", 27, "Mumbai"),
            new User(5, "ben", 20, "Kolkata")
    );

    private final Map<String, List<Order>> map = Map.of(
            "sam", List.of(
                    new Order(UUID.randomUUID(), "sam-product-1"),
                    new Order(UUID.randomUUID(), "sam-product-2")
            ),
            "mike", List.of(
                    new Order(UUID.randomUUID(), "mike-product-1"),
                    new Order(UUID.randomUUID(), "mike-product-2"),
                    new Order(UUID.randomUUID(), "mike-product-3")
            )
    );

    private Flux<Order> ordersByUserName(String name) {
        return Flux.fromIterable(map.getOrDefault(name, Collections.emptyList()));
    }

    private Flux<List<Order>> ordersByUsers(List<String> names) {
        return Flux.fromIterable(names)
                .map(name -> map.getOrDefault(name, Collections.emptyList()));
    }

    private Flux<List<Order>> ordersByUsers2(List<String> names) {
        return Flux.fromIterable(names)
                .flatMapSequential(s -> Mono.justOrEmpty(map.get(s))
                        .delayElement(Duration.ofMillis(ThreadLocalRandom.current().nextInt(1, 100)))
                        .defaultIfEmpty(List.of())
                );
    }

    private Mono<Map<User, List<Order>>> ordersByUsersUsingMap(List<User> users) {
        return Flux.fromIterable(users)
                .map(user -> Tuples.of(user, map.getOrDefault(user.name(), Collections.emptyList())))
                .collectMap(
                        Tuple2::getT1,
                        Tuple2::getT2
                );
    }

    @SchemaMapping(typeName = "Query")
//    @QueryMapping
    public Flux<User> users() {
        return userFlux;
    }

    /*@SchemaMapping(typeName = "User")
    public Flux<Order> orders(User user) {
        log.info("Fetching Order for user: {}", user.name());
        return ordersByUserName(user.name());
    }*/

   /* @SchemaMapping(typeName = "User", field = "orders")
    public Flux<Order> orders(User user) {
        return ordersByUserName(user.name());
    }*/

    /*@BatchMapping(typeName = "User") // List Way // N+1 problem fix
    public Flux<List<Order>> orders(List<User> users) {
        log.info("Fetching Order for users");
        return ordersByUsers(users.stream().map(User::name).toList());
    }*/

    /*@BatchMapping(typeName = "User")
    public Flux<List<Order>> orders(List<User> users) {
        log.info("Fetching Order For users");
        return ordersByUsers2(users.stream().map(User::name).toList());
    }*/

    @BatchMapping(typeName = "User") // Map Way
    public Mono<Map<User, List<Order>>> orders(List<User> users) {
        log.info("Fetching user orders");
        return ordersByUsersUsingMap(users);
    }

//    @SchemaMapping(typeName = "User")  // Overriding a field -> age
//    public Mono<Integer> age() {
//        return Mono.just(100);
//    }

//    @SchemaMapping(typeName = "Query")
//    public Flux<User> users(DataFetchingFieldSelectionSet selectionSet) {
//        log.info("SelectionSet: {}", selectionSet.getFields());
//        return userFlux;
//    }

//    @SchemaMapping(typeName = "Query")
//    public Flux<User> users(DataFetchingEnvironment dataFetchingEnvironment) {
//        log.info("DataFetchingEnvironment: {}", dataFetchingEnvironment.getDocument());
//        return userFlux;
//    }
}
