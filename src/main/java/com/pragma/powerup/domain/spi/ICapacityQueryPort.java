package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityQueryPort {
    Mono<Long> count();
    Flux<Capacity> findByName(int page, int size, boolean descending);
    Flux<Capacity> findAll();
}
