package com.pragma.powerup.domain.api;

import java.util.List;
import reactor.core.publisher.Mono;

public interface ICapacityDeleteServicePort {
    Mono<Void> deleteCapacities(List<Long> ids);
}
