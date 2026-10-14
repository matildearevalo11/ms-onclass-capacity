package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.response.CapacityListItemDto;
import com.pragma.powerup.application.dto.response.CapacityPageResponseDto;
import com.pragma.powerup.application.dto.response.TechnologySummaryDto;
import com.pragma.powerup.domain.model.CapacityPage;
import com.pragma.powerup.domain.model.CapacityPageQuery;
import org.springframework.stereotype.Component;

@Component
public class CapacityPageMapper {
    public CapacityPageQuery toQuery(CapacityPageRequestDto request) {
        return new CapacityPageQuery(request.getPage(), request.getSize(), request.getSortBy(),
                "desc".equals(request.getDirection()));
    }

    public CapacityPageResponseDto toResponse(CapacityPage page) {
        return new CapacityPageResponseDto(page.getContent().stream()
                .map(item -> new CapacityListItemDto(item.getId(), item.getName(), item.getDescription(),
                        item.getTechnologies().stream()
                                .map(technology -> new TechnologySummaryDto(technology.getId(), technology.getName()))
                                .toList()))
                .toList(), page.getPage(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
