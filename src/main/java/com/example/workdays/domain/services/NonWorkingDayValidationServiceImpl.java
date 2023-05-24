package com.example.workdays.domain.services;

import com.example.workdays.domain.entities.NonWorkingDay;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class NonWorkingDayValidationServiceImpl implements NonWorkingDayValidationService {

    private final Validator validator;

    public NonWorkingDayValidationServiceImpl() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Override
    public Set<ConstraintViolation<NonWorkingDay>> validate(NonWorkingDay nonWorkingDay) {
        return validator.validate(nonWorkingDay);
    }

    @Override
    public Set<ConstraintViolation<NonWorkingDay>> validateAll(Collection<NonWorkingDay> nonWorkingDays) {
        return nonWorkingDays.stream()
                .flatMap(nwd -> this.validate(nwd).stream())
                .collect(Collectors.toSet());
    }
}
