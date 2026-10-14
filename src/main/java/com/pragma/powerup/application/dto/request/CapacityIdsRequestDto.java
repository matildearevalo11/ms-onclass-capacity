package com.pragma.powerup.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapacityIdsRequestDto {
    @NotEmpty(message = "Capacity IDs are required.")
    private List<@NotNull @Positive Long> ids;
}
