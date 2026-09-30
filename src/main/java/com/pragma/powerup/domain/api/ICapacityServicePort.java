package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<Capacity> createCapacity(Capacity capacity);
}
