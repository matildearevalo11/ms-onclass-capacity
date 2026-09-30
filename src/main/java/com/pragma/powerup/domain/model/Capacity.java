package com.pragma.powerup.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Capacity {
    private final Long id;
    private final String name;
    private final String description;
    private final List<Long> technologyIds;
}
