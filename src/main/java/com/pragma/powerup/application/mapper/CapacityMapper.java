package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.dto.response.CapacityResponseDto;
import com.pragma.powerup.domain.model.Capacity;
import org.springframework.stereotype.Component;

@Component
public class CapacityMapper {
    public Capacity toModel(CapacityRequestDto request) {
        return new Capacity(null, request.getName(), request.getDescription(), request.getTechnologyIds());
    }

    public CapacityResponseDto toResponse(Capacity capacity) {
        return new CapacityResponseDto(capacity.getId(), capacity.getName(), capacity.getDescription(), capacity.getTechnologyIds());
    }
}
