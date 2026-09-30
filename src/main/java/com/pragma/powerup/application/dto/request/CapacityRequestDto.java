package com.pragma.powerup.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CapacityRequestDto {

    @NotBlank(message = "Capacity name is required.")
    private String name;
    @NotBlank(message = "Capacity description is required.")
    private String description;
    @NotNull(message = "Technology IDs are required.")
    @Size(min = 3, max = 20, message = "A capacity must have between 3 and 20 technologies.")
    private List<Long> technologyIds;

    @JsonIgnore
    @AssertTrue(message = "Technologies must not be repeated.")
    public boolean isTechnologyIdsUnique() {
        return technologyIds == null || new HashSet<>(technologyIds).size() == technologyIds.size();
    }
}
