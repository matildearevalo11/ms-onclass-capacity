package com.pragma.powerup.infrastructure.out.rest.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TechnologyAssociationRequestDto {
    private final Long capacityId;
    private final List<Long> technologyIds;
}
