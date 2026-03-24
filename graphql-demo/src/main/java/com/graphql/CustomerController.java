package com.graphql;

import com.graphql.dto.AgeRangeFilter;
import com.graphql.dto.Customer;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class CustomerController {

    private final Flux<Customer> customerFlux = Flux.just(
            new Customer(1, "sam", 23, "London"),
            new Customer(2, "john", 21, "Berlin"),
            new Customer(3, "jake", 25, "Delhi"),
            new Customer(4, "mike", 27, "Mumbai"),
            new Customer(5, "ben", 20, "Kolkata")
    );

    @QueryMapping
    public Flux<Customer> customers() {
        return customerFlux;
    }

    @QueryMapping
    public Mono<Customer> customerById(@Argument int id) {
        return customerFlux.filter(customer -> customer.id() == id)
                .next();
    }

    @QueryMapping
    public Flux<Customer> customersNameContains(@Argument String name) {
        return customerFlux.filter(customer -> customer.name().contains(name));
    }

    @QueryMapping
    public Flux<Customer> customersByAgeRange(@Argument AgeRangeFilter filter) {
        return customerFlux.filter(customer -> customer.age() >= filter.minAge() && customer.age() <= filter.maxAge());
    }
}
