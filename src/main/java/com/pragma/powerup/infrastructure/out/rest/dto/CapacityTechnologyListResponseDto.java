package com.pragma.powerup.infrastructure.out.rest.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapacityTechnologyListResponseDto {
    private Long capacityId;
    private List<TechnologySummaryResponseDto> technologies;
}
