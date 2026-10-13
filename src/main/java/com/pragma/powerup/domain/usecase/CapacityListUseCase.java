package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICapacityListServicePort;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.model.CapacityListItem;
import com.pragma.powerup.domain.model.CapacityPage;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import com.pragma.powerup.domain.model.TechnologySummary;
import com.pragma.powerup.domain.spi.ICapacityQueryPort;
import com.pragma.powerup.domain.spi.ITechnologyQueryPort;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class CapacityListUseCase implements ICapacityListServicePort {
    private final ICapacityQueryPort capacityPort;
    private final ITechnologyQueryPort technologyPort;

    @Override
    public Flux<Long> existingIds(List<Long> ids) {
        return capacityPort.findExistingIds(ids);
    }

    @Override
    public Mono<List<CapacityListItem>> detailsByIds(List<Long> ids) {
        return capacityPort.findByIds(ids).collectList()
                .flatMap(capacities -> technologyPort.findByCapacityIds(
                                capacities.stream().map(Capacity::getId).toList())
                        .map(technologies -> capacities.stream()
                                .map(capacity -> new CapacityListItem(capacity.getId(), capacity.getName(),
                                        capacity.getDescription(),
                                        List.copyOf(technologies.getOrDefault(capacity.getId(), List.of()))))
                                .toList()));
    }

    @Override
    public Mono<CapacityPage> list(CapacityPageQuery query) {
        return Mono.zip(capacityPort.count(), "name".equals(query.getSortBy())
                        ? capacityPort.findByName(query.getPage(), query.getSize(), query.isDescending()).collectList()
                        : capacityPort.findAll().collectList())
                .flatMap(result -> technologyPort.findByCapacityIds(result.getT2().stream().map(Capacity::getId).toList())
                        .map(technologies -> toPage(result.getT2(), technologies, result.getT1(), query)));
    }

    private CapacityPage toPage(List<Capacity> capacities, Map<Long, List<TechnologySummary>> technologies,
                                long total, CapacityPageQuery query) {
        List<CapacityListItem> items = new ArrayList<>(capacities.stream()
                .map(capacity -> new CapacityListItem(capacity.getId(), capacity.getName(), capacity.getDescription(),
                        List.copyOf(technologies.getOrDefault(capacity.getId(), List.of()))))
                .toList());
        if ("technologyCount".equals(query.getSortBy())) {
            Comparator<CapacityListItem> order = Comparator.comparingInt((CapacityListItem item) -> item.getTechnologies().size())
                    .thenComparing(CapacityListItem::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(CapacityListItem::getId);
            items.sort(query.isDescending() ? order.reversed() : order);
            long offset = (long) query.getPage() * query.getSize();
            items = offset >= items.size() ? List.of()
                    : items.subList((int) offset, (int) Math.min(offset + query.getSize(), items.size()));
        }
        long pages = total / query.getSize() + (total % query.getSize() == 0 ? 0 : 1);
        return new CapacityPage(items, query.getPage(), query.getSize(), total, pages);
    }
}
