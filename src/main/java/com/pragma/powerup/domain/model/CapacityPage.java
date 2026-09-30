package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CapacityPage {
    private final List<CapacityListItem> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final long totalPages;
}
