package com.example.workdays.adapters.api.v1;

import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.exceptions.InvalidDayException;
import com.example.workdays.configuration.spring.ApiConfiguration;
import com.example.workdays.configuration.spring.DomainConfiguration;
import com.example.workdays.configuration.spring.GatewaysConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ApiConfiguration.class, DomainConfiguration.class, GatewaysConfiguration.class})
public class WorkdaysApiSpringIntegrationTest {

    @Autowired
    private WorkdaysApi workdaysApi;


    public static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of("2021-01-01", "2021-01-31", 20),
                Arguments.of("2022-06-27", "2022-07-04", 5)
        );
    }

    @ParameterizedTest
    @MethodSource("source")
    public void givenDefaultHolidays_whenCalled_returnsExpected(String from, String till, int expected) {
        WorkdaysCalculator calculator = workdaysApi.getWorkdaysCalculator();
        int actual = calculator.workdaysBetweenInclusive(from, till);
        assertEquals(expected, actual);
    }

    @Test
    public void givenInalidDay_whenAdded_throwsException() {
        assertThrows(InvalidDayException.class, () -> workdaysApi.getNonWorkingDaysManager().add(new Day("", "")));
    }

    @Test
    public void givenDefaultHolidays_whenAllDaysAreListed_returnsNotEmpty() {
        WorkdaysApi.WorkdaysApiBuilder.addDefaultNonWorkingDays(workdaysApi.getNonWorkingDaysManager());
        assertFalse(workdaysApi.getNonWorkingDaysManager().getAll().isEmpty());
    }

    @Test
    public void givenDefaultHolidays_whenSaved_CouldBeLoaded() {
        workdaysApi.getNonWorkingDaysPersister().saveData();
        workdaysApi.getNonWorkingDaysPersister().loadData();
    }

    @Test
    public void givenDefaultHolidays_whenRemoved_CouldBeReloaded() {
        workdaysApi.getNonWorkingDaysPersister().saveData();
        workdaysApi.getNonWorkingDaysManager().remove(new Day("1 0 0 04 07 ? *", "Independence Day"));

        int actual = workdaysApi.getWorkdaysCalculator().workdaysBetweenInclusive("2022-06-27", "2022-07-04");
        assertEquals(6, actual);

        workdaysApi.getNonWorkingDaysPersister().loadData();
        actual = workdaysApi.getWorkdaysCalculator().workdaysBetweenInclusive("2022-06-27", "2022-07-04");
        assertEquals(5, actual);
    }
}
