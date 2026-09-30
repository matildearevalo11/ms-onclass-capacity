package com.pragma.powerup.application.dto.request;

import jakarta.validation.Validation;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CapacityRequestDtoTest {
    @Test
    void acceptsBoundariesAndRejectsInvalidAssociations() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var request = new CapacityRequestDto();
            request.setName("Backend"); request.setDescription("Backend development");
            for (int count : new int[] {3, 20}) {
                request.setTechnologyIds(LongStream.rangeClosed(1, count).boxed().toList());
                assertThat(validator.validate(request)).isEmpty();
            }
            for (var ids : List.of(List.of(1L, 2L), LongStream.rangeClosed(1, 21).boxed().toList(),
                    List.of(1L, 1L, 2L), List.of(0L, 2L, 3L), java.util.Arrays.asList(1L, null, 3L))) {
                request.setTechnologyIds(ids);
                assertThat(validator.validate(request)).isNotEmpty();
            }
            request.setTechnologyIds(null);
            assertThat(validator.validate(request)).isNotEmpty();
        }
    }
}
