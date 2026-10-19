package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.enums.CapacitySortField;
import com.pragma.powerup.domain.enums.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CapacityPageQuery {
    private final int page;
    private final int size;
    private final CapacitySortField sortField;
    private final SortDirection direction;

    public boolean isDescending() {
        return direction == SortDirection.DESC;
    }

    public long offset() {
        return (long) page * size;
    }

    public long totalPages(long totalElements) {
        return Math.ceilDiv(totalElements, size);
    }
}
