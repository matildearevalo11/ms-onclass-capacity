package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICapacityServicePort;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import com.pragma.powerup.domain.util.ApplicationConstants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import static com.pragma.powerup.domain.util.DomainValidations.requirePositiveIds;
import static com.pragma.powerup.domain.util.DomainValidations.requireSizeBetween;
import static com.pragma.powerup.domain.util.DomainValidations.requireText;
import static com.pragma.powerup.domain.util.DomainValidations.requireUnique;

@RequiredArgsConstructor
public class CapacityUseCase implements ICapacityServicePort {
    private final ICapacityPersistencePort persistencePort;
    private final ITechnologyAssociationPort technologyPort;

    @Override
    public Mono<Capacity> createCapacity(Capacity capacity) {
        return Mono.fromCallable(() -> validate(capacity))
                .flatMap(persistencePort::save)
                .flatMap(saved -> technologyPort.associate(saved).thenReturn(saved));
    }

    private Capacity validate(Capacity capacity) {
        String name = requireText(capacity.getName(), ExceptionMessages.CAPACITY_NAME_REQUIRED);
        String description = requireText(capacity.getDescription(), ExceptionMessages.CAPACITY_DESCRIPTION_REQUIRED);
        List<Long> technologyIds = requireSizeBetween(capacity.getTechnologyIds(),
                ApplicationConstants.MIN_TECHNOLOGIES_PER_CAPACITY.getValue(),
                ApplicationConstants.MAX_TECHNOLOGIES_PER_CAPACITY.getValue(),
                ExceptionMessages.TECHNOLOGY_COUNT_OUT_OF_RANGE);
        requirePositiveIds(technologyIds, ExceptionMessages.TECHNOLOGY_ID_INVALID);
        requireUnique(technologyIds, ExceptionMessages.TECHNOLOGIES_REPEATED);
        return new Capacity(capacity.getId(), name, description, List.copyOf(technologyIds));
    }
}
