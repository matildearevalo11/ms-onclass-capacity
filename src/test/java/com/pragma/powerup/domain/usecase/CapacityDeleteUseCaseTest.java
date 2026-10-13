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
    void deletesCapacitiesOnlyAfterTechnologyCleanupSucceeds() {
        var capacities = mock(ICapacityPersistencePort.class);
        var technologies = mock(ITechnologyAssociationPort.class);
        var ids = List.of(1L, 2L);
        when(technologies.deleteForCapacities(ids)).thenReturn(Mono.empty());
        when(capacities.deleteByIds(ids)).thenReturn(Mono.empty());

        StepVerifier.create(new CapacityDeleteUseCase(capacities, technologies).deleteCapacities(ids))
                .verifyComplete();
        var order = inOrder(technologies, capacities);
        order.verify(technologies).deleteForCapacities(ids);
        order.verify(capacities).deleteByIds(ids);
    }

    @Test
    void keepsCapacitiesWhenTechnologyCleanupFails() {
        var capacities = mock(ICapacityPersistencePort.class);
        var technologies = mock(ITechnologyAssociationPort.class);
        var ids = List.of(1L);
        when(technologies.deleteForCapacities(ids)).thenReturn(Mono.error(new IllegalStateException("Unavailable")));
        when(capacities.deleteByIds(ids)).thenReturn(Mono.empty());

        StepVerifier.create(new CapacityDeleteUseCase(capacities, technologies).deleteCapacities(ids))
                .expectError(IllegalStateException.class).verify();
        verify(capacities, never()).deleteByIds(ids);
    }
}
