package com.pragma.powerup.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CapacityPageResponseDto {
    private final List<CapacityListItemDto> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final long totalPages;
}
