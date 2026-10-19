package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ValidationException;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CapacityUseCaseTest {
    private final ICapacityPersistencePort persistence = mock(ICapacityPersistencePort.class);
    private final ITechnologyAssociationPort technology = mock(ITechnologyAssociationPort.class);
    private final CapacityUseCase useCase = new CapacityUseCase(persistence, technology);

    @Test
    void savesNormalizedCapacityAndThenAssociatesTechnologies() {
        when(persistence.save(any(Capacity.class))).thenAnswer(invocation -> {
            Capacity capacity = invocation.getArgument(0);
            return Mono.just(new Capacity(7L, capacity.getName(), capacity.getDescription(), capacity.getTechnologyIds()));
        });
        when(technology.associate(any(Capacity.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createCapacity(new Capacity(null, " Backend ", " Development ", List.of(1L, 2L, 3L))))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(7L);
                    assertThat(saved.getName()).isEqualTo("Backend");
                    assertThat(saved.getDescription()).isEqualTo("Development");
                }).verifyComplete();
        var order = inOrder(persistence, technology);
        order.verify(persistence).save(any(Capacity.class));
        order.verify(technology).associate(any(Capacity.class));
    }

    @Test
    void acceptsTheTechnologyCountLimits() {
        when(persistence.save(any(Capacity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(technology.associate(any(Capacity.class))).thenReturn(Mono.empty());

        for (int count : new int[] {3, 20}) {
            var ids = LongStream.rangeClosed(1, count).boxed().toList();
            StepVerifier.create(useCase.createCapacity(new Capacity(null, "Backend", "Development", ids)))
                    .expectNextCount(1).verifyComplete();
        }
    }

    @Test
    void rejectsInvalidCapacitiesWithoutSavingOrAssociating() {
        var invalid = Map.of(
                new Capacity(null, " ", "Development", List.of(1L, 2L, 3L)), ExceptionMessages.CAPACITY_NAME_REQUIRED,
                new Capacity(null, "Backend", null, List.of(1L, 2L, 3L)), ExceptionMessages.CAPACITY_DESCRIPTION_REQUIRED,
                new Capacity(null, "Backend", "Development", List.of(1L, 2L)), ExceptionMessages.TECHNOLOGY_COUNT_OUT_OF_RANGE,
                new Capacity(null, "Backend", "Development", LongStream.rangeClosed(1, 21).boxed().toList()),
                ExceptionMessages.TECHNOLOGY_COUNT_OUT_OF_RANGE,
                new Capacity(null, "Backend", "Development", null), ExceptionMessages.TECHNOLOGY_COUNT_OUT_OF_RANGE,
                new Capacity(null, "Backend", "Development", Arrays.asList(1L, null, 3L)), ExceptionMessages.TECHNOLOGY_ID_INVALID,
                new Capacity(null, "Backend", "Development", List.of(1L, 1L, 2L)), ExceptionMessages.TECHNOLOGIES_REPEATED);

        invalid.forEach((capacity, message) -> StepVerifier.create(useCase.createCapacity(capacity))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(ValidationException.class).hasMessage(message.getMessage()))
                .verify());
        verifyNoInteractions(persistence, technology);
    }

    @Test
    void propagatesAssociationFailureWithoutReturningSuccess() {
        var error = new IllegalStateException("Service unavailable");
        when(persistence.save(any(Capacity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(technology.associate(any(Capacity.class))).thenReturn(Mono.error(error));

        StepVerifier.create(useCase.createCapacity(new Capacity(1L, "Backend", "Development", List.of(1L, 2L, 3L))))
                .expectErrorMatches(actual -> actual == error).verify();
    }
}
