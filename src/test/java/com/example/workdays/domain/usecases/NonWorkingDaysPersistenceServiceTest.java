package com.example.workdays.domain.usecases;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.services.InvalidNonWorkingDayException;
import com.example.workdays.domain.services.NonWorkingDayValidationService;
import com.example.workdays.domain.usecases.persistence.NonWorkingDaysPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NonWorkingDaysPersistenceServiceTest {
    @Mock
    private NonWorkingDaysRepository repository;
    @Mock
    private NonWorkingDaysPersistencePort persistencePort;
    @Mock
    private NonWorkingDayValidationService validationService;
    NonWorkingDaysPersistenceService unit;

    @BeforeEach
    public void init() {
        unit = new NonWorkingDaysPersistenceService(persistencePort, repository, validationService);
    }

    @Test
    public void givenNonWorkingDay_WhenDataExported_ThenSaved() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 1 2 ? 2023", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        doNothing().when(persistencePort).save(any());

        unit.exportData();

        verify(persistencePort, times(1)).save(eq(nonWorkingDays));
    }

    @Test
    public void givenNonWorkingDay_WhenDataImported_ThenLoaded() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 1 2 ? 2023", "test"));
        when(persistencePort.load()).thenReturn(nonWorkingDays);
        when(validationService.validateAll(any())).thenReturn(Set.of());
        doNothing().when(repository).addAll(any());

        unit.importData();

        verify(repository, times(1)).addAll(eq(nonWorkingDays));
    }

    @Test
    public void givenInvalidNonWorkingDay_WhenDataImported_ThenNotLoaded() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("invalid", "test"));
        when(persistencePort.load()).thenReturn(nonWorkingDays);

        ConstraintViolation<NonWorkingDay> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("expression");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("invalid expression");
        when(validationService.validateAll(any())).thenReturn(Set.of(violation));

        assertThrows(InvalidNonWorkingDayException.class, () -> unit.importData());
        verify(repository, times(0)).addAll(eq(nonWorkingDays));

    }

}
