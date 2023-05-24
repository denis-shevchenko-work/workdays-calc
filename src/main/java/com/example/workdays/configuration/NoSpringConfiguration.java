package com.example.workdays.configuration;

import com.example.workdays.adapters.api.v1.WorkdaysApi;
import com.example.workdays.adapters.api.v1.NonWorkingDaysManager;
import com.example.workdays.adapters.api.v1.NonWorkingDaysPersister;
import com.example.workdays.adapters.api.v1.WorkdaysCalculator;
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
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class NoSpringConfiguration {

    public static final String DEFAULT_WORKDAYS_REPOSITORY_JSON = "~/.workdays/repository.json";

    public static WorkdaysApi buildDefault() {
        String pathToRepositoryFile = DEFAULT_WORKDAYS_REPOSITORY_JSON;
        Properties prop = new Properties();
        InputStream inputStream = NoSpringConfiguration.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            prop.load(inputStream);
            pathToRepositoryFile = prop.getProperty("workday.gateway.persistence.file.pathToRepository", DEFAULT_WORKDAYS_REPOSITORY_JSON);
        } catch (IOException e) {
            log.error("Error loading application.properties", e);
        }

        return buildDefault(pathToRepositoryFile);
    }

    public static WorkdaysApi buildDefault(String pathToRepositoryFile) {
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
        NonWorkingDaysManager nonWorkingDaysManager =
                new NonWorkingDaysManager(manageNonWorkingDayUseCase, dayMapper, dayValidationService);
        NonWorkingDaysPersister nonWorkingDaysPersister =
                new NonWorkingDaysPersister(domainPersistenceService, domainPersistenceService);
        return WorkdaysApi.builder()
                .nonWorkingDaysManager(nonWorkingDaysManager)
                .nonWorkingDaysPersister(nonWorkingDaysPersister)
                .workdaysCalculator(workdaysCalculator)
                .build();
    }

}
