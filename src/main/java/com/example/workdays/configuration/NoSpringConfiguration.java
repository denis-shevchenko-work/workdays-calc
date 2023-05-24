package com.example.workdays.configuration;

import com.example.workdays.adapters.api.v1.Api;
import com.example.workdays.adapters.api.v1.NonWorkingDaysManager;
import com.example.workdays.adapters.api.v1.NonWorkingDaysPersister;
import com.example.workdays.adapters.api.v1.WorkdaysCalculator;
import com.example.workdays.adapters.api.v1.entities.Day;
import com.example.workdays.adapters.api.v1.mappers.DayMapper;
import com.example.workdays.adapters.api.v1.validators.DayValidationService;
import com.example.workdays.adapters.gateways.NonWorkingDayInMemoryRepository;
import com.example.workdays.adapters.gateways.NonWorkingDaysJsonFilePersistenceAdaptorService;
import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.services.NonWorkingDayValidationService;
import com.example.workdays.domain.services.NonWorkingDayValidationServiceImpl;
import com.example.workdays.domain.usecases.calculate.CalculateWorkDaysUseCase;
import com.example.workdays.domain.usecases.calculate.WorkdaysCalculatorService;
import com.example.workdays.domain.usecases.manage.ManageNonWorkingDaysUseCase;
import com.example.workdays.domain.usecases.manage.NonWorkingDaysManagingService;
import com.example.workdays.domain.usecases.persistence.NonWorkingDaysPersistenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.mapstruct.factory.Mappers;

import java.nio.file.Path;
import java.util.Set;

public class NoSpringConfiguration {

    public static Api buildDefault(String pathToRepositoryFile) {
        //adapters
        pathToRepositoryFile = pathToRepositoryFile.replace("~", System.getProperty("user.home"));
        ObjectMapper objectMapper = new ObjectMapper() {{
            SimpleModule module = new SimpleModule();
            module.addDeserializer(NonWorkingDay.class,
                    new NonWorkingDaysJsonFilePersistenceAdaptorService.NonWorkingDayDeserializer());
            registerModule(module);
        }};


        NonWorkingDaysPersistencePort workingDaysPersistencePort =
                new NonWorkingDaysJsonFilePersistenceAdaptorService(Path.of(pathToRepositoryFile), objectMapper);
        NonWorkingDaysRepository nonWorkingDaysRepository =
                new NonWorkingDayInMemoryRepository(Set.of());
        NonWorkingDayValidationService domainValidationService =
                new NonWorkingDayValidationServiceImpl();

        //domain
        DayMapper dayMapper = Mappers.getMapper(DayMapper.class);
        DayValidationService dayValidationService = new DayValidationService();
        CalculateWorkDaysUseCase workDaysUseCase = new WorkdaysCalculatorService(nonWorkingDaysRepository);
        ManageNonWorkingDaysUseCase manageNonWorkingDayUseCase = new NonWorkingDaysManagingService(
                nonWorkingDaysRepository);
        NonWorkingDaysPersistenceService domainPersistenceService = new NonWorkingDaysPersistenceService(
                workingDaysPersistencePort, nonWorkingDaysRepository, domainValidationService);

        //api
        WorkdaysCalculator workdaysCalculator = new WorkdaysCalculator(workDaysUseCase);
        NonWorkingDaysManager workingDaysManager =
                new NonWorkingDaysManager(manageNonWorkingDayUseCase, dayMapper, dayValidationService);
        NonWorkingDaysPersister workingDaysPersister =
                new NonWorkingDaysPersister(domainPersistenceService, domainPersistenceService);

        return new Api(workdaysCalculator, workingDaysPersister, workingDaysManager);
    }

    public static void addDefaultNonWorkingDays(NonWorkingDaysManager nonWorkingDaysManager) {
        //default configuration
        nonWorkingDaysManager.add(new Day("1 0 0 ? * SAT,SUN *", "Weekend"));
        nonWorkingDaysManager.add(new Day("1 0 0 31 12 ? *", "New Year's eve"));
        nonWorkingDaysManager.add(new Day("1 0 0 1 1 ? *", "New Year"));
        nonWorkingDaysManager.add(new Day("1 0 0 25 12 ? *", "Christmas"));
        nonWorkingDaysManager.add(new Day("1 0 0 04 07 ? *", "Independence Day"));
    }
}
