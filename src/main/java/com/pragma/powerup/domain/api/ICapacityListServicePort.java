package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.CapacityPage;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import reactor.core.publisher.Mono;

public interface ICapacityListServicePort {
    Mono<CapacityPage> list(CapacityPageQuery query);
}
