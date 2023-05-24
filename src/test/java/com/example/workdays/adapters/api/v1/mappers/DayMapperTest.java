package com.example.workdays.adapters.api.v1.mappers;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.mappers.DayMapper;
import com.example.workdays.domain.entities.NonWorkingDay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class DayMapperTest {

    DayMapper unit = Mappers.getMapper(DayMapper.class);

    @Test
    public void givenWithRegularEntity_whenMapToDto_thenMapped() {
        NonWorkingDay entity = new NonWorkingDay("testExpression", "testName");
        Day expectedDto = new Day("testExpression", "testName");
        Day actualDto = unit.toDto(entity);
        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    public void givenWithNull_whenMapToDto_thenReturnsNull() {
        Assertions.assertNull(unit.toDto((NonWorkingDay) null));
    }

    @Test
    public void givenWithNullDtoCollection_whenMapToDto_thenReturnsNull() {
        Assertions.assertNull(unit.toDto((Collection<NonWorkingDay>) null));
    }

    @Test
    public void givenWithNull_whenMapFromDto_thenReturnsNull() {
        Assertions.assertNull(unit.fromDto(null));
    }

    @Test
    public void givenWithRegularDto_whenMapFromDto_thenMapped() {
        Day dto = new Day("testExpression", "testName");
        NonWorkingDay expectedEntity = new NonWorkingDay("testExpression", "testName");
        NonWorkingDay actualEntity = unit.fromDto(dto);
        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

}
