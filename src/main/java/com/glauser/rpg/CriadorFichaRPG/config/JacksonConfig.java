
package com.glauser.rpg.CriadorFichaRPG.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        /*
         * Permite trabalhar com LocalDate, LocalDateTime,
         * LocalTime, etc.
         */
        mapper.registerModule(new JavaTimeModule());

        /*
         * Mantém a configuração atual do projeto.
         */
        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategies.LOWER_CAMEL_CASE
        );

        /*
         * Ignora propriedades desconhecidas durante
         * a leitura dos JSONs.
         */
        mapper.disable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );

        return mapper;
    }
}

