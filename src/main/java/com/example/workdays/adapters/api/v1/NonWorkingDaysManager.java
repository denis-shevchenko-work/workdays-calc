package com.example.workdays.adapters.api.v1;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.exceptions.InvalidDayException;
import com.example.workdays.adapters.api.v1.mappers.DayMapper;
import com.example.workdays.adapters.api.v1.validators.DayValidationService;
import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.usecases.manage.ManageNonWorkingDaysUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class NonWorkingDaysManager {
    private final ManageNonWorkingDaysUseCase manageNonWorkingDaysUseCase;
    private final DayMapper dayMapper;
    private final DayValidationService dayValidationService;

    public Collection<Day> getAll() {
        return dayMapper.toDto(manageNonWorkingDaysUseCase.getAllNonWorkingDays());
    }

    public void add(Day day) {
        validate(day);
        NonWorkingDay nonWorkingDay = dayMapper.fromDto(day);
        manageNonWorkingDaysUseCase.addNonWorkingDay(nonWorkingDay);
    }

    private void validate(Day day) {
        Set<ConstraintViolation<Day>> violations = dayValidationService.validate(day);
        if (!violations.isEmpty()) {
            log.error("Invalid day: {}", violations.stream()
                    .map(violation -> String.format("%s:%s", violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.joining(", ")));
            throw new InvalidDayException(violations);
        }
    }

    public void remove(Day day) {
        NonWorkingDay nonWorkingDay = dayMapper.fromDto(day);
        manageNonWorkingDaysUseCase.removeNonWorkingDay(nonWorkingDay);
    }

}
