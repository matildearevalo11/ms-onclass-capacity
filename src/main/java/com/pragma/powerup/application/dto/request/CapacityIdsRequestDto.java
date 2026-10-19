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
    @NotEmpty(message = ValidationMessages.CAPACITY_IDS_REQUIRED)
    private List<@NotNull(message = ValidationMessages.CAPACITY_ID_REQUIRED)
            @Positive(message = ValidationMessages.CAPACITY_ID_POSITIVE) Long> ids;
}
