package com.example.workdays.configuration.spring;

import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.example.workdays.domain.services.NonWorkingDayValidationService;
import com.example.workdays.domain.services.NonWorkingDayValidationServiceImpl;
import com.example.workdays.domain.usecases.calculate.CalculateWorkDaysUseCase;
import com.example.workdays.domain.usecases.calculate.WorkdaysCalculatorService;
import com.example.workdays.domain.usecases.manage.ManageNonWorkingDaysUseCase;
import com.example.workdays.domain.usecases.manage.NonWorkingDaysManagingService;
import com.example.workdays.domain.usecases.persistence.NonWorkingDaysPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    public CalculateWorkDaysUseCase calculateWorkDaysUseCase(NonWorkingDaysRepository nonWorkingDaysRepository) {
        return new WorkdaysCalculatorService(nonWorkingDaysRepository);
    }

    @Bean
    public ManageNonWorkingDaysUseCase manageNonWorkingDaysUseCase (NonWorkingDaysRepository nonWorkingDaysRepository) {
        return new NonWorkingDaysManagingService(nonWorkingDaysRepository);
    }

    @Bean
    public NonWorkingDaysPersistenceService persistenceService(
            NonWorkingDaysPersistencePort persistencePort,
            NonWorkingDaysRepository repository,
            NonWorkingDayValidationService validationService) {
        return new NonWorkingDaysPersistenceService(persistencePort, repository, validationService);
    }

    @Bean
    NonWorkingDayValidationService validationService() {
        return new NonWorkingDayValidationServiceImpl();
    }

}
