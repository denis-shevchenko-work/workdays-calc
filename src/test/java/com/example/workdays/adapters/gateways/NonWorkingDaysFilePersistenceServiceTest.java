package com.example.workdays.adapters.gateways;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NonWorkingDaysFilePersistenceServiceTest {

    NonWorkingDaysJsonFilePersistenceAdaptorService unit;
    private static final ObjectMapper mapper = new ObjectMapper();


    @BeforeAll
    public static void init() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(NonWorkingDay.class, new NonWorkingDaysJsonFilePersistenceAdaptorService.NonWorkingDayDeserializer());
        mapper.registerModule(module);
    }

    @Test
    public void givenEmptyNonWorkingDayFile_WhenLoaded_ThrowsException() {
        String empty = ClassLoader.getSystemResource("persist/empty.json").getPath();
        unit = new NonWorkingDaysJsonFilePersistenceAdaptorService(Path.of(empty), mapper);
        assertThrows(RuntimeException.class, () -> unit.load());
    }

    @Test
    public void givenWeekendsNonWorkingDayFile_WhenLoaded_ReturnsEmptyCollection() {
        String empty = ClassLoader.getSystemResource("persist/weekends.json").getPath();
        unit = new NonWorkingDaysJsonFilePersistenceAdaptorService(Path.of(empty), mapper);
        List<NonWorkingDay> expected = List.of(new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekends"));
        List<NonWorkingDay> actual = unit.load();
        assertIterableEquals(expected, actual);
    }

    @Test
    public void givenEmptyFileAndWeekends_WhenSaved_FileContainsWeekends() throws IOException {
        File tempFile = File.createTempFile("weekend", ".json.tmp");
        tempFile.deleteOnExit();
        unit = new NonWorkingDaysJsonFilePersistenceAdaptorService(Path.of(tempFile.getPath()), mapper);
        List<NonWorkingDay> given = List.of(
                new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekends")
        );

        unit.save(given);

        List<NonWorkingDay> actual = unit.load();
        assertIterableEquals(given, actual);
    }

    @Test
    public void givenEmptyNonExistingFileAndWeekends_WhenSaved_FileContainsWeekends() throws IOException {
        File tempFile = File.createTempFile("weekend", ".json.tmp");
        String tempFilePath = tempFile.getPath();
        assertTrue(tempFile.delete());
        unit = new NonWorkingDaysJsonFilePersistenceAdaptorService(Path.of(tempFilePath), mapper);
        List<NonWorkingDay> given = List.of(
                new NonWorkingDay("1 0 0 ? * SAT,SUN *", "weekends")
        );

        unit.save(given);

        List<NonWorkingDay> actual = unit.load();
        assertIterableEquals(given, actual);
    }
}
