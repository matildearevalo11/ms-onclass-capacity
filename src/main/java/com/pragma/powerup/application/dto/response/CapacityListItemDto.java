package com.pragma.powerup.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CapacityListItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final List<TechnologySummaryDto> technologies;
}
