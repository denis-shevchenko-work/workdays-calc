package com.example.workdays.domain.usecases;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.usecases.manage.NonWorkingDaysManagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NonWorkingDaysManagingServiceTest {
    @Mock
    private NonWorkingDaysRepository repository;

    NonWorkingDaysManagingService unit;

    @BeforeEach
    public void init() {
        unit = new NonWorkingDaysManagingService(repository);
    }

    @Test
    public void givenNonWorkingDaysFromRepository_WhenGetAll_ThenReturned() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 1 2 ? 2023", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        Collection<NonWorkingDay> actual = unit.getAllNonWorkingDays();
        assertIterableEquals(nonWorkingDays, actual);
    }

    @Test
    public void givenNonWorkingDaysFromRepository_WhenNewDayAdded_ThenAddedToRepo() {
        NonWorkingDay day = new NonWorkingDay("1 0 0 1 3 ? 2023", "new");
        unit.addNonWorkingDay(day);
        verify(repository, times(1)).add(eq(day));
    }

    @Test
    public void givenNonWorkingDaysFromRepository_WhenSomeDayRemoved_ThenRemovedFromRepo() {
        NonWorkingDay day = new NonWorkingDay("1 0 0 1 2 ? 2023", "test");
        unit.removeNonWorkingDay(day);
        verify(repository, times(1)).remove(eq(day));
    }
}
