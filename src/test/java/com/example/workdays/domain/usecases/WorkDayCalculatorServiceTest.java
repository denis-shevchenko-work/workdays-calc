package com.example.workdays.domain.usecases;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.usecases.calculate.WorkdaysCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkDayCalculatorServiceTest {

    @Mock
    private NonWorkingDaysRepository repository;
    private WorkdaysCalculatorService unit;

    @BeforeEach
    public void init() {
        unit = new WorkdaysCalculatorService(repository);
    }

    @Test
    public void givenNonWorkingDay_WhenWorkingDaysCalculatedForThisDay_ThenReturns0() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 1 2 ? 2023", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        LocalDate from = LocalDate.of(2023, 2, 1);
        LocalDate till = LocalDate.of(2023, 2, 1);
        assertEquals(0, unit.workdaysBetweenInclusive(from, till), "NonWorkingDay should not be returned as working one");
    }

    @Test
    public void givenNonWorkingDay_WhenWorkingDaysCalculatedForANextDay_ThenReturns1() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 1 3 ? 2023", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        LocalDate from = LocalDate.of(2023, 3, 2);
        LocalDate till = LocalDate.of(2023, 3, 2);
        assertEquals(1, unit.workdaysBetweenInclusive(from, till), "WorkingDay should be returned as working one");
    }

    @Test
    public void givenAllDaysAreWorking_WhenWorkingDaysCalculatedForSomeDay_ThenReturns1() {
        List<NonWorkingDay> nonWorkingDays = List.of();
        when(repository.getAll()).thenReturn(nonWorkingDays);
        LocalDate from = LocalDate.of(2023, 2, 1);
        LocalDate till = LocalDate.of(2023, 2, 1);
        assertEquals(1, unit.workdaysBetweenInclusive(from, till), "WorkingDay should be returned as working one");
    }

    @Test
    public void givenAllDaysAreWorking_WhenWorkingDaysCalculatedForWrongRange_ThenThrows() {
        LocalDate from = LocalDate.of(2023, 2, 2);
        LocalDate till = LocalDate.of(2023, 2, 1);
        assertThrows(IllegalArgumentException.class, () -> unit.workdaysBetweenInclusive(from, till), "WorkingDay should be returned as working one");
    }

    @Test
    public void givenNonWorkingDay_WhenWorkingDaysCalculatedForThisAndNextDay_ThenReturns1() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 2 2 ? 2023", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        LocalDate from = LocalDate.of(2023, 2, 1);
        LocalDate till = LocalDate.of(2023, 2, 2);
        assertEquals(1, unit.workdaysBetweenInclusive(from, till), "NonWorkingDay should be excluded");
    }

    @Test
    public void givenNonWorkingWeekend_WhenWorkingDaysCalculatedForThisWeek_ThenReturns5() {
        List<NonWorkingDay> nonWorkingDays = List.of(new NonWorkingDay("1 0 0 ? * SAT,SUN *", "test"));
        when(repository.getAll()).thenReturn(nonWorkingDays);
        LocalDate monday = LocalDate.of(2023, 2, 15);
        LocalDate sunday = LocalDate.of(2023, 2, 21);
        assertEquals(5, unit.workdaysBetweenInclusive(monday, sunday), "NonWorkingDays should be excluded");
    }


    public static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekend"),
                                new NonWorkingDay("1 0 0 31 12 ? *", "new year's eve"),// 31/12/2022 is Saturday
                                new NonWorkingDay("1 0 0 1 1 ? *", "new year")// 1/1/2023 is Sunday
                        ),
                        LocalDate.of(2022, 12, 26),//monday
                        LocalDate.of(2023, 1, 1),//sunday
                        5
                )
        );
    }


    @ParameterizedTest
    @MethodSource("source")
    public void parametrizedTest(List<NonWorkingDay> nonWorkingDays, LocalDate from, LocalDate till, int expected) {
        when(repository.getAll()).thenReturn(nonWorkingDays);
        assertEquals(expected, unit.workdaysBetweenInclusive(from, till),
                String.format("Wrong result for period %s:%s and non working days %s",
                        from.format(ISO_LOCAL_DATE),
                        till.format(ISO_LOCAL_DATE),
                        toString(nonWorkingDays, NonWorkingDay::getExpression)));

    }

    private static String toString(List<NonWorkingDay> nonWorkingDays, Function<NonWorkingDay, String> expression) {
        return nonWorkingDays.stream().map(expression).collect(joining(", "));
    }
}
