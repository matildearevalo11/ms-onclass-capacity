package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICapacityServicePort;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CapacityUseCase implements ICapacityServicePort {
    private final ICapacityPersistencePort persistencePort;
    private final ITechnologyAssociationPort technologyPort;

    @Override
    public Mono<Capacity> createCapacity(Capacity capacity) {
        return persistencePort.save(capacity)
                .flatMap(saved -> technologyPort.associate(saved).thenReturn(saved));
    }
}
