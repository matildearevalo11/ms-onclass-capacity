package com.pragma.powerup.infrastructure.out.r2dbc.repository;

import com.pragma.powerup.infrastructure.out.r2dbc.entity.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.List;

public interface ICapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
    @Query("SELECT id FROM capacities WHERE id IN (:ids)")
    Flux<Long> findExistingIds(List<Long> ids);

    @Query("SELECT id, name, description FROM capacities ORDER BY name ASC, id ASC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findPageByNameAsc(int size, long offset);

    @Query("SELECT id, name, description FROM capacities ORDER BY name DESC, id DESC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findPageByNameDesc(int size, long offset);
}
