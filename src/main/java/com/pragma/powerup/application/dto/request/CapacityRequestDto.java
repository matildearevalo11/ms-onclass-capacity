package com.pragma.powerup.application.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CapacityRequestDto {
    private String name;
    private String description;
    private List<Long> technologyIds;
}
