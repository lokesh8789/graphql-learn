package com.graphql;

import com.graphql.dto.Order;
import com.graphql.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @SchemaMapping(typeName = "Query")
//    @QueryMapping
    public Flux<User> users() {
        return userFlux;
    }

    @SchemaMapping(typeName = "User")
    public Flux<Order> orders(User user) {
        log.info("Fetching Order for user: {}", user.name());
        return ordersByUserName(user.name());
    }

   /* @SchemaMapping(typeName = "User", field = "orders")
    public Flux<Order> orders(User user) {
        return ordersByUserName(user.name());
    }*/
}
