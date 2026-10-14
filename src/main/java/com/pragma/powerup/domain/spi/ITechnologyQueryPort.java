package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.TechnologySummary;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface ITechnologyQueryPort {
    Mono<Map<Long, List<TechnologySummary>>> findByCapacityIds(List<Long> capacityIds);
}
