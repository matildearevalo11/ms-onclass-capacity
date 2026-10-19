package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICapacityListServicePort;
import com.pragma.powerup.domain.enums.CapacitySortField;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.model.CapacityListItem;
import com.pragma.powerup.domain.model.CapacityPage;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import com.pragma.powerup.domain.model.TechnologySummary;
import com.pragma.powerup.domain.spi.ICapacityQueryPort;
import com.pragma.powerup.domain.spi.ITechnologyQueryPort;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class CapacityListUseCase implements ICapacityListServicePort {
    private static final Comparator<CapacityListItem> BY_TECHNOLOGY_COUNT =
            Comparator.comparingInt((CapacityListItem item) -> item.getTechnologies().size())
                    .thenComparing(CapacityListItem::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(CapacityListItem::getId);

    private final ICapacityQueryPort capacityPort;
    private final ITechnologyQueryPort technologyPort;

    @Override
    public Flux<Long> existingIds(List<Long> ids) {
        return capacityPort.findExistingIds(ids);
    }

    @Override
    public Mono<List<CapacityListItem>> detailsByIds(List<Long> ids) {
        return capacityPort.findByIds(ids).collectList().flatMap(this::withTechnologies);
    }

    @Override
    public Mono<CapacityPage> list(CapacityPageQuery query) {
        return Mono.zip(capacityPort.count(), findCandidates(query))
                .flatMap(totalAndCapacities -> withTechnologies(totalAndCapacities.getT2())
                        .map(items -> toPage(sortAndSlice(items, query), totalAndCapacities.getT1(), query)));
    }

    private Mono<List<Capacity>> findCandidates(CapacityPageQuery query) {
        return switch (query.getSortField()) {
            case NAME -> capacityPort.findByName(query.getPage(), query.getSize(), query.isDescending()).collectList();
            case TECHNOLOGY_COUNT -> capacityPort.findAll().collectList();
        };
    }

    private Mono<List<CapacityListItem>> withTechnologies(List<Capacity> capacities) {
        List<Long> capacityIds = capacities.stream().map(Capacity::getId).toList();
        return technologyPort.findByCapacityIds(capacityIds)
                .map(technologies -> capacities.stream()
                        .map(capacity -> toItem(capacity, technologies.getOrDefault(capacity.getId(), List.of())))
                        .toList());
    }

    private List<CapacityListItem> sortAndSlice(List<CapacityListItem> items, CapacityPageQuery query) {
        if (query.getSortField() != CapacitySortField.TECHNOLOGY_COUNT) {
            return items;
        }
        return items.stream()
                .sorted(query.isDescending() ? BY_TECHNOLOGY_COUNT.reversed() : BY_TECHNOLOGY_COUNT)
                .skip(query.offset())
                .limit(query.getSize())
                .toList();
    }

    private CapacityListItem toItem(Capacity capacity, List<TechnologySummary> technologies) {
        return new CapacityListItem(capacity.getId(), capacity.getName(), capacity.getDescription(),
                List.copyOf(technologies));
    }

    private CapacityPage toPage(List<CapacityListItem> items, long total, CapacityPageQuery query) {
        return new CapacityPage(items, query.getPage(), query.getSize(), total, query.totalPages(total));
    }
}
