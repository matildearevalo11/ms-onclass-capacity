package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.enums.CapacitySortField;
import com.pragma.powerup.domain.enums.SortDirection;
import com.pragma.powerup.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityPageQueryTest {
    @Test
    void calculatesOffsetAndRoundsTotalPagesUp() {
        var query = new CapacityPageQuery(2, 10, CapacitySortField.NAME, SortDirection.ASC);

        assertThat(query.offset()).isEqualTo(20);
        assertThat(query.totalPages(0)).isZero();
        assertThat(query.totalPages(20)).isEqualTo(2);
        assertThat(query.totalPages(21)).isEqualTo(3);
    }

    @Test
    void resolvesSortFieldFromRequestValue() {
        assertThat(CapacitySortField.fromValue("name")).isEqualTo(CapacitySortField.NAME);
        assertThat(CapacitySortField.fromValue("technologyCount")).isEqualTo(CapacitySortField.TECHNOLOGY_COUNT);
        assertThatThrownBy(() -> CapacitySortField.fromValue("date")).isInstanceOf(ValidationException.class);
        assertThat(SortDirection.fromValue("desc")).isEqualTo(SortDirection.DESC);
        assertThatThrownBy(() -> SortDirection.fromValue("up")).isInstanceOf(ValidationException.class);
    }
}
