package com.example.workdays.adapters.api.v1.exceptions;

import com.example.workdays.adapters.api.v1.entities.Day;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class InvalidDayException extends RuntimeException {

    public InvalidDayException(Set<ConstraintViolation<Day>> violations) {
        super(violations.stream()
                .map(violation -> String.format("%s:%s", violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.joining(", ", "Invalid day: ", "")));
    }

}
