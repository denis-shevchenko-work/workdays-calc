package com.example.workdays.configuration.spring;

import com.example.workdays.adapters.api.v1.Api;
import com.example.workdays.adapters.api.v1.NonWorkingDaysManager;
import com.example.workdays.adapters.api.v1.NonWorkingDaysPersister;
import com.example.workdays.adapters.api.v1.WorkdaysCalculator;
import com.example.workdays.adapters.api.v1.mappers.DayMapper;
import com.example.workdays.adapters.api.v1.validators.DayValidationService;
import com.example.workdays.domain.usecases.calculate.CalculateWorkDaysUseCase;
import com.example.workdays.domain.usecases.manage.ManageNonWorkingDaysUseCase;
import com.example.workdays.domain.usecases.persistence.ExportNonWorkingDaysUseCase;
import com.example.workdays.domain.usecases.persistence.ImportNonWorkingDaysUseCase;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfiguration {

    @Bean
    public WorkdaysCalculator workdaysCalculator(CalculateWorkDaysUseCase calculateWorkDaysUseCase) {
        return new WorkdaysCalculator(calculateWorkDaysUseCase);
    }

    @Bean
    public NonWorkingDaysManager nonWorkingDaysManager(
            ManageNonWorkingDaysUseCase manageNonWorkingDaysUseCase,
            DayMapper dayMapper,
            DayValidationService dayValidationService) {
        return new NonWorkingDaysManager(manageNonWorkingDaysUseCase, dayMapper, dayValidationService);
    }

    @Bean
    public NonWorkingDaysPersister nonWorkingDaysPersister(
            ImportNonWorkingDaysUseCase importNonWorkingDaysUseCase,
            ExportNonWorkingDaysUseCase exportNonWorkingDaysUseCase) {
        return new NonWorkingDaysPersister(importNonWorkingDaysUseCase, exportNonWorkingDaysUseCase);
    }

    @Bean
    public Api api(
            WorkdaysCalculator workdaysCalculator,
            NonWorkingDaysPersister nonWorkingDaysPersister,
            NonWorkingDaysManager nonWorkingDaysManager,
            @Value("${workday.api.addDefaultHolidays:false}") Boolean addDefaultHolidays) {
        Api.ApiBuilder builder = Api.builder()
                .nonWorkingDaysManager(nonWorkingDaysManager)
                .nonWorkingDaysPersister(nonWorkingDaysPersister)
                .workdaysCalculator(workdaysCalculator);
        if (addDefaultHolidays) {
                builder.addDefaultHolidays();
        }
        return builder.build();
    }

    @Bean
    DayMapper dayMapper() {
        return Mappers.getMapper(DayMapper.class);
    }

    @Bean
    public DayValidationService dayValidationService() {
        return new DayValidationService();
    }

}
