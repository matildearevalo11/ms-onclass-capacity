package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import com.pragma.powerup.domain.model.TechnologySummary;
import com.pragma.powerup.domain.spi.ICapacityQueryPort;
import com.pragma.powerup.domain.spi.ITechnologyQueryPort;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

class CapacityListUseCaseTest {
    @Test
    void sortsByTechnologyCountBeforeTakingRequestedPage() {
        var capacities = mock(ICapacityQueryPort.class);
        var technologies = mock(ITechnologyQueryPort.class);
        var backend = new Capacity(1L, "Backend", "Services", List.of());
        var frontend = new Capacity(2L, "Frontend", "Interfaces", List.of());
        var cloud = new Capacity(3L, "Cloud", "Infrastructure", List.of());
        when(capacities.count()).thenReturn(Mono.just(3L));
        when(capacities.findAll()).thenReturn(Flux.just(backend, frontend, cloud));
        when(technologies.findByCapacityIds(List.of(1L, 2L, 3L))).thenReturn(Mono.just(Map.of(
                1L, List.of(new TechnologySummary(1L, "Java"), new TechnologySummary(2L, "SQL")),
                2L, List.of(new TechnologySummary(3L, "React")),
                3L, List.of(new TechnologySummary(4L, "AWS"), new TechnologySummary(5L, "Docker"),
                        new TechnologySummary(6L, "Linux")))));

        StepVerifier.create(new CapacityListUseCase(capacities, technologies)
                        .list(new CapacityPageQuery(1, 1, "technologyCount", true)))
                .assertNext(page -> {
                    org.assertj.core.api.Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
                    org.assertj.core.api.Assertions.assertThat(page.getTotalPages()).isEqualTo(3);
                    org.assertj.core.api.Assertions.assertThat(page.getContent()).hasSize(1);
                    org.assertj.core.api.Assertions.assertThat(page.getContent().getFirst().getName()).isEqualTo("Backend");
                    org.assertj.core.api.Assertions.assertThat(page.getContent().getFirst().getTechnologies()).hasSize(2);
                }).verifyComplete();
        verify(capacities, never()).findByName(anyInt(), anyInt(), anyBoolean());
    }
}
