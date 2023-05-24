package com.example.workdays.adapters.api.v1.validators;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.validators.DayValidationService;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DayValidatorTest {
    DayValidationService unit = new DayValidationService();

    @Test
    public void givenWithRegularDay_whenValidate_thenNoViolations() {
        Set<ConstraintViolation<Day>> actual = unit.validate(new Day("testExpression", "testName"));
        assertTrue(actual.isEmpty());
    }

    @Test
    public void givenWithBlankDayName_whenValidate_thenViolations() {
        Set<ConstraintViolation<Day>> actual = unit.validate(new Day("testExpression", ""));
        assertEquals(1, actual.size());
        ConstraintViolation<Day> violation = actual.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("may not be empty", violation.getMessage());
    }

    @Test
    public void givenWithBlankDayExpression_whenValidate_thenViolations() {
        Set<ConstraintViolation<Day>> actual = unit.validate(new Day("", "testName"));
        assertEquals(1, actual.size());
        ConstraintViolation<Day> violation = actual.iterator().next();
        assertEquals("expression", violation.getPropertyPath().toString());
        assertEquals("may not be empty", violation.getMessage());
    }
}
