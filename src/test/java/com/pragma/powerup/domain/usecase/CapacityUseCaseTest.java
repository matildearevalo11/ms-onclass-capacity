package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

class CapacityUseCaseTest {
    @Test
    void returnsSavedCapacityOnlyAfterAssociatingTechnologies() {
        var persistence = mock(ICapacityPersistencePort.class);
        var technology = mock(ITechnologyAssociationPort.class);
        var input = new Capacity(null, "Backend", "Development", List.of(1L, 2L, 3L));
        var saved = new Capacity(7L, input.getName(), input.getDescription(), input.getTechnologyIds());
        when(persistence.save(input)).thenReturn(Mono.just(saved));
        when(technology.associate(saved)).thenReturn(Mono.empty());
        StepVerifier.create(new CapacityUseCase(persistence, technology).createCapacity(input))
                .expectNext(saved).verifyComplete();
        verify(technology).associate(saved);
    }

    @Test
    void propagatesAssociationFailureWithoutReturningSuccess() {
        var persistence = mock(ICapacityPersistencePort.class);
        var technology = mock(ITechnologyAssociationPort.class);
        var capacity = new Capacity(1L, "Backend", "Development", List.of(1L, 2L, 3L));
        var error = new IllegalStateException("Service unavailable");
        when(persistence.save(capacity)).thenReturn(Mono.just(capacity));
        when(technology.associate(capacity)).thenReturn(Mono.error(error));
        StepVerifier.create(new CapacityUseCase(persistence, technology).createCapacity(capacity))
                .expectErrorMatches(actual -> actual == error).verify();
    }
}
