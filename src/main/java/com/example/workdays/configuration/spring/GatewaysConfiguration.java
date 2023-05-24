package com.example.workdays.configuration.spring;

import com.example.workdays.adapters.gateways.NonWorkingDayInMemoryRepository;
import com.example.workdays.adapters.gateways.NonWorkingDaysJsonFilePersistenceAdaptorService;
import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.example.workdays.domain.ports.NonWorkingDaysRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Set;

@Configuration
public class GatewaysConfiguration {

    @Bean
    ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(NonWorkingDay.class,
                new NonWorkingDaysJsonFilePersistenceAdaptorService.NonWorkingDayDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Bean
    NonWorkingDaysPersistencePort persistencePort(
            @Value("${gateway.persistence.file.pathToRepository}") String pathToRepository,
            ObjectMapper mapper) {
        Path path = Path.of(pathToRepository.replace("~", System.getProperty("user.home")));
        return new NonWorkingDaysJsonFilePersistenceAdaptorService(path, mapper);
    }

    @Bean
    public NonWorkingDaysRepository repository() {
        return new NonWorkingDayInMemoryRepository(Set.of());
    }


}
