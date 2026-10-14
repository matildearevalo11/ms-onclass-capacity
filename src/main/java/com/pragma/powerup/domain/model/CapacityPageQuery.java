package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CapacityPageQuery {
    private final int page;
    private final int size;
    private final String sortBy;
    private final boolean descending;
}
