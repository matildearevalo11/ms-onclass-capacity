package com.pragma.powerup.application.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CapacityResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final List<Long> technologyIds;
}
