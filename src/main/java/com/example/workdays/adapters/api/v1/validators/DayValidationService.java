package com.example.workdays.adapters.api.v1.validators;

import com.example.workdays.adapters.api.v1.entities.Day;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class DayValidationService {
    private final Validator validator;

    public DayValidationService() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public Set<ConstraintViolation<Day>> validate(Day day) {
        return validator.validate(day);
    }
}
