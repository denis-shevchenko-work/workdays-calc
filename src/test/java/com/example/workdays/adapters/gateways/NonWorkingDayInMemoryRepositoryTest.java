package com.example.workdays.adapters.gateways;

import com.example.workdays.domain.entities.NonWorkingDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class NonWorkingDayInMemoryRepositoryTest {
    NonWorkingDayInMemoryRepository unit;

    @BeforeEach
    public void init() {
        unit = new NonWorkingDayInMemoryRepository(Set.of());
    }

    @Test
    public void givenEmptyRepository_whenDayIsAdded_thenDayIsListed() {
        NonWorkingDay day = new NonWorkingDay("test", "test");
        unit.add(day);
        assertEquals(1, unit.getAll().size());
        assertIterableEquals(Set.of(day), unit.getAll());
    }

    @Test
    public void givenRepositoryWithADay_whenTheSameDayIsAdded_thenOnlyThisDayIsListed() {
        NonWorkingDay day = new NonWorkingDay("test", "test");
        unit.add(day);
        unit.add(day);
        assertEquals(1, unit.getAll().size());
        assertIterableEquals(Set.of(day), unit.getAll());
    }

    @Test
    public void givenRepositoryWithADay_whenADayWithTheSameExpressionIsAdded_thenOnlyThisDayIsListed() {
        NonWorkingDay day1 = new NonWorkingDay("testExpression", "testName1");
        NonWorkingDay day2 = new NonWorkingDay("testExpression", "testName2");

        unit.add(day1);
        unit.add(day2);
        assertEquals(1, unit.getAll().size());
        assertIterableEquals(Set.of(day1), unit.getAll());
    }

    @Test
    public void givenRepositoryWithADay_whenADayWithTheSameNameIsAdded_thenAllDaysAreListed() {
        NonWorkingDay day1 = new NonWorkingDay("testExpression1", "testName");
        NonWorkingDay day2 = new NonWorkingDay("testExpression2", "testName");

        unit.add(day1);
        unit.add(day2);
        assertEquals(2, unit.getAll().size());
        assertIterableEquals(List.of(day1, day2), unit.getAll());
    }

    @Test
    public void givenRepositoryWithADay_whenThisDayIsRemoved_thenNoDaysAreListed() {
        NonWorkingDay day = new NonWorkingDay("test", "test");
        unit.add(day);
        unit.remove(day);
        assertEquals(0, unit.getAll().size());
    }
}
