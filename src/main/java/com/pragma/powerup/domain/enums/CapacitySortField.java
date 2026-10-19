package com.pragma.powerup.domain.enums;

import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ValidationException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CapacitySortField {
    NAME("name"),
    TECHNOLOGY_COUNT("technologyCount");

    private final String value;

    public static CapacitySortField fromValue(String value) {
        return Arrays.stream(values())
                .filter(field -> field.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ExceptionMessages.INVALID_SORT_FIELD.getMessage()));
    }
}
