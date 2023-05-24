package com.example.workdays.domain.usecases.persistence;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.services.InvalidNonWorkingDayException;
import com.example.workdays.domain.services.NonWorkingDayValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class NonWorkingDaysPersistenceService implements ImportNonWorkingDaysUseCase, ExportNonWorkingDaysUseCase {
    private final NonWorkingDaysPersistencePort persistencePort;
    private final NonWorkingDaysRepository repository;
    private final NonWorkingDayValidationService validationService;

    @Override
    public void exportData() {
        persistencePort.save(repository.getAll());
    }

    @Override
    public void importData() {
        Collection<NonWorkingDay> nonWorkingDays = persistencePort.load();
        validate(nonWorkingDays);
        repository.addAll(nonWorkingDays);
    }

    private void validate(Collection<NonWorkingDay> nonWorkingDays) {
        Set<ConstraintViolation<NonWorkingDay>> violations = validationService.validateAll(nonWorkingDays);
        if (!violations.isEmpty()) {
            log.error("Invalid day: {}", violations.stream()
                    .map(violation -> String.format("%s:%s", violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.joining(", ")));
            throw new InvalidNonWorkingDayException(violations);
        }
    }
}
