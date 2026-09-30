package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CapacityListItem {
    private final Long id;
    private final String name;
    private final String description;
    private final List<TechnologySummary> technologies;
}
