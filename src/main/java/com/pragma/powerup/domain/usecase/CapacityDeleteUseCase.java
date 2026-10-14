package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICapacityDeleteServicePort;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CapacityDeleteUseCase implements ICapacityDeleteServicePort {
    private final ICapacityPersistencePort capacities;
    private final ITechnologyAssociationPort technologies;

    @Override
    public Mono<Void> deleteCapacities(List<Long> ids) {
        return technologies.deleteForCapacities(ids).then(Mono.defer(() -> capacities.deleteByIds(ids)));
    }
}
