package com.pragma.powerup.infrastructure.out.r2dbc.adapter;

import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.infrastructure.out.r2dbc.mapper.CapacityEntityMapper;
import com.pragma.powerup.infrastructure.out.r2dbc.repository.ICapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CapacityR2dbcAdapter implements ICapacityPersistencePort {
    private final ICapacityRepository repository;
    private final CapacityEntityMapper mapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return repository.save(mapper.toEntity(capacity))
                .map(saved -> mapper.toModel(saved, capacity.getTechnologyIds()));
    }
}
