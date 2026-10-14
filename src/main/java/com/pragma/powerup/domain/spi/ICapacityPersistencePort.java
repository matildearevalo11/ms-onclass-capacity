package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity capacity);
}
