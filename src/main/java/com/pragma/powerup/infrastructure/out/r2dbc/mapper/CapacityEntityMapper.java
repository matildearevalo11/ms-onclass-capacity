package com.pragma.powerup.infrastructure.out.r2dbc.mapper;

import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.infrastructure.out.r2dbc.entity.CapacityEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CapacityEntityMapper {
    public CapacityEntity toEntity(Capacity capacity) {
        return new CapacityEntity(null, capacity.getName(), capacity.getDescription());
    }

    public Capacity toModel(CapacityEntity entity, List<Long> technologyIds) {
        return new Capacity(entity.getId(), entity.getName(), entity.getDescription(), technologyIds);
    }
}
