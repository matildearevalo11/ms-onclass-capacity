package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.CapacityPage;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityListServicePort {
    Mono<CapacityPage> list(CapacityPageQuery query);
    Flux<Long> existingIds(List<Long> ids);
}
