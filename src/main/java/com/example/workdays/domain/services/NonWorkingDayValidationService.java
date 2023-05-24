package com.example.workdays.domain.services;

import com.example.workdays.domain.entities.NonWorkingDay;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;

public interface NonWorkingDayValidationService {
    Set<ConstraintViolation<NonWorkingDay>> validate(NonWorkingDay nonWorkingDay);

    Set<ConstraintViolation<NonWorkingDay>> validateAll(Collection<NonWorkingDay> nonWorkingDays);
}
