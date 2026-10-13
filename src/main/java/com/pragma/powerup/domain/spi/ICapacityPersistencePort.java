package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity capacity);
    Mono<Void> deleteByIds(List<Long> ids);
}
