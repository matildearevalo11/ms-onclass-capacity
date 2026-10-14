package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Capacity;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ITechnologyAssociationPort {
    Mono<Void> associate(Capacity capacity);
    Mono<Void> deleteForCapacities(List<Long> capacityIds);
}
