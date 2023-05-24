package com.example.workdays.configuration;

import com.example.workdays.adapters.api.v1.Api;
import com.example.workdays.adapters.api.v1.WorkdaysCalculator;
import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.exceptions.InvalidDayException;
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
@SpringBootTest
public class ApiSpringIntegrationTest {

    @Autowired
    Api integration;


    public static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of("2021-01-01", "2021-01-31", 20),
                Arguments.of("2022-06-27", "2022-07-04", 5)
        );
    }

    @ParameterizedTest
    @MethodSource("source")
    public void givenDefaultIntegration_whenCalled_returnsExpected(String from, String till, int expected) {
        WorkdaysCalculator calculator = integration.getWorkdaysCalculator();
        int actual = calculator.workdaysBetweenInclusive(from, till);
        assertEquals(expected, actual);
    }

    @Test
    public void givenInalidDay_whenAdded_throwsException() {
        assertThrows(InvalidDayException.class, () -> integration.getNonWorkingDaysManager().add(new Day("", "")));
    }

    @Test
    public void givenDefaultIntegration_whenAllDaysAreListed_returnsNotEmpty() {
        assertFalse(integration.getNonWorkingDaysManager().getAll().isEmpty());
    }

    @Test
    public void givenDefaultIntegration_whenSaved_CouldBeLoaded() {
        integration.getNonWorkingDaysPersister().saveData();
        integration.getNonWorkingDaysPersister().loadData();
    }

    @Test
    public void givenDefaultIntegration_whenRemoved_CouldBeReloaded() {
        integration.getNonWorkingDaysPersister().saveData();
        integration.getNonWorkingDaysManager().remove(new Day("1 0 0 04 07 ? *", "Independence Day"));

        int actual = integration.getWorkdaysCalculator().workdaysBetweenInclusive("2022-06-27", "2022-07-04");
        assertEquals(6, actual);

        integration.getNonWorkingDaysPersister().loadData();
        actual = integration.getWorkdaysCalculator().workdaysBetweenInclusive("2022-06-27", "2022-07-04");
        assertEquals(5, actual);
    }
}
