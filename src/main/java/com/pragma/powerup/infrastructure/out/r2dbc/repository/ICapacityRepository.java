package com.pragma.powerup.infrastructure.out.r2dbc.repository;

import com.pragma.powerup.infrastructure.out.r2dbc.entity.CapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ICapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
}
