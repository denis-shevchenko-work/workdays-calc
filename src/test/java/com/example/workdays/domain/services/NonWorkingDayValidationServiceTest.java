package com.example.workdays.domain.services;

import com.example.workdays.domain.entities.NonWorkingDay;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonWorkingDayValidationServiceTest {
    NonWorkingDayValidationService unit = new NonWorkingDayValidationServiceImpl();

    @Test
    public void givenRegularDay_whenValidate_thenNoViolations() {
        Set<ConstraintViolation<NonWorkingDay>> actual = unit.validate(new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekend"));
        assertTrue(actual.isEmpty());
    }

    @Test
    public void givenRegularDays_whenValidateAll_thenNoViolations() {
        Set<ConstraintViolation<NonWorkingDay>> actual = unit.validateAll(List.of(
                new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekend"),
                new NonWorkingDay("1 0 0 1 2 ? 2023", "new year")));
        assertTrue(actual.isEmpty());
    }

    @Test
    public void givenBadExpressionDay_whenValidate_thenViolations() {
        Set<ConstraintViolation<NonWorkingDay>> actual = unit.validate(new NonWorkingDay("? * SAT,SUN *", "weekend"));
        assertEquals(1, actual.size());
        ConstraintViolation<NonWorkingDay> violation = actual.iterator().next();
        assertEquals("expression", violation.getPropertyPath().toString());
        assertEquals("Cron expression contains 4 parts but we expect one of [6, 7]", violation.getMessage());
    }

    @Test
    public void givenBadNameDay_whenValidate_thenViolations() {
        Set<ConstraintViolation<NonWorkingDay>> actual = unit.validate(new NonWorkingDay("1 0 0 ? * SAT,SUN *", ""));
        assertEquals(1, actual.size());
        ConstraintViolation<NonWorkingDay> violation = actual.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Name cannot be blank", violation.getMessage());
    }
}
