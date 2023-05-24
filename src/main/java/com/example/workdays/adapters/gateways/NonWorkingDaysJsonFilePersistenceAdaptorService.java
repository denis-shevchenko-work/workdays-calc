package com.example.workdays.adapters.gateways;

import com.example.workdays.domain.entities.NonWorkingDay;
import com.example.workdays.domain.ports.NonWorkingDaysPersistencePort;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class NonWorkingDaysJsonFilePersistenceAdaptorService implements NonWorkingDaysPersistencePort {

    private final Path path;
    private final ObjectMapper mapper;

    @Override
    public void save(Collection<NonWorkingDay> days) {
        File file = path.toFile();
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()){
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            mapper.writeValue(file, days);
        } catch (IOException e) {
            log.error("Cannot write to file {}", path, e);
            throw new RuntimeException("Cannot write to file " + path, e);
        }
    }

    @Override
    public List<NonWorkingDay> load() {
        try {
            return mapper.readValue(path.toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Cannot read from file {}", path, e);
            throw new RuntimeException("Cannot read from file " + path, e);
        }
    }


    public static class NonWorkingDayDeserializer extends JsonDeserializer<NonWorkingDay> {
        @Override
        public NonWorkingDay deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String expression = node.get("expression").asText();
            String name = node.get("name").asText();
            return new NonWorkingDay(expression, name);
        }
    }

}
