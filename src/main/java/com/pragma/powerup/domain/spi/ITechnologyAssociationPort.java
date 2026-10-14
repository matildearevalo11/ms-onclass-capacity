package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ITechnologyAssociationPort {
    Mono<Void> associate(Capacity capacity);
}
