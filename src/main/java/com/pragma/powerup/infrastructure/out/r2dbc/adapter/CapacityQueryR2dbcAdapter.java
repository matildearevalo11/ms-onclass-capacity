package com.pragma.powerup.infrastructure.out.r2dbc.adapter;

import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ICapacityQueryPort;
import com.pragma.powerup.infrastructure.out.r2dbc.mapper.CapacityEntityMapper;
import com.pragma.powerup.infrastructure.out.r2dbc.repository.ICapacityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CapacityQueryR2dbcAdapter implements ICapacityQueryPort {
    private final ICapacityRepository repository;
    private final CapacityEntityMapper mapper;

    @Override
    public Flux<Long> findExistingIds(List<Long> ids) {
        return repository.findExistingIds(ids);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    public Flux<Capacity> findByName(int page, int size, boolean descending) {
        long offset = (long) page * size;
        var rows = descending ? repository.findPageByNameDesc(size, offset)
                : repository.findPageByNameAsc(size, offset);
        return rows.map(entity -> mapper.toModel(entity, List.of()));
    }

    @Override
    public Flux<Capacity> findAll() {
        return repository.findAll().map(entity -> mapper.toModel(entity, List.of()));
    }
}
