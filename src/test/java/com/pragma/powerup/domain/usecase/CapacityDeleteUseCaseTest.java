package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

class CapacityDeleteUseCaseTest {
    @Test
    void deletesCapacitiesBeforeCleaningUpTheirTechnologies() {
        var capacities = mock(ICapacityPersistencePort.class);
        var technologies = mock(ITechnologyAssociationPort.class);
        var ids = List.of(1L, 2L);
        when(technologies.deleteForCapacities(ids)).thenReturn(Mono.empty());
        when(capacities.deleteByIds(ids)).thenReturn(Mono.empty());

        StepVerifier.create(new CapacityDeleteUseCase(capacities, technologies).deleteCapacities(ids))
                .verifyComplete();
        var order = inOrder(capacities, technologies);
        order.verify(capacities).deleteByIds(ids);
        order.verify(technologies).deleteForCapacities(ids);
    }

    @Test
    void propagatesTechnologyCleanupFailureSoTheLocalDeletionIsRolledBack() {
        var capacities = mock(ICapacityPersistencePort.class);
        var technologies = mock(ITechnologyAssociationPort.class);
        var ids = List.of(1L);
        when(capacities.deleteByIds(ids)).thenReturn(Mono.empty());
        when(technologies.deleteForCapacities(ids)).thenReturn(Mono.error(new IllegalStateException("Unavailable")));

        StepVerifier.create(new CapacityDeleteUseCase(capacities, technologies).deleteCapacities(ids))
                .expectError(IllegalStateException.class).verify();
        verify(capacities).deleteByIds(ids);
    }

    @Test
    void doesNotCallTechnologyServiceWhenLocalDeletionFails() {
        var capacities = mock(ICapacityPersistencePort.class);
        var technologies = mock(ITechnologyAssociationPort.class);
        var ids = List.of(1L);
        when(capacities.deleteByIds(ids)).thenReturn(Mono.error(new IllegalStateException("Database down")));

        StepVerifier.create(new CapacityDeleteUseCase(capacities, technologies).deleteCapacities(ids))
                .expectError(IllegalStateException.class).verify();
        verifyNoInteractions(technologies);
    }
}
