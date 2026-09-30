package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.dto.response.CapacityResponseDto;
import com.pragma.powerup.domain.model.Capacity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CapacityMapper {
    public Capacity toModel(CapacityRequestDto request) {
        return new Capacity(null, request.getName().strip(), request.getDescription().strip(),
                List.copyOf(request.getTechnologyIds()));
    }

    public CapacityResponseDto toResponse(Capacity capacity) {
        return new CapacityResponseDto(capacity.getId(), capacity.getName(), capacity.getDescription(), capacity.getTechnologyIds());
    }
}
