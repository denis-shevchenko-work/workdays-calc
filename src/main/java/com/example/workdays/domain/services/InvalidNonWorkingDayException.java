package com.example.workdays.domain.services;

import com.example.workdays.domain.entities.NonWorkingDay;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class InvalidNonWorkingDayException extends RuntimeException {
    public InvalidNonWorkingDayException(Set<ConstraintViolation<NonWorkingDay>> violations) {
        super(violations.stream()
                .map(violation -> String.format("%s:%s", violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.joining(", ", "Invalid day: ", "")));
    }

}
