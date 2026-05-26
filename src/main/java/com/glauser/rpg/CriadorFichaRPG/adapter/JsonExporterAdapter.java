package com.glauser.rpg.CriadorFichaRPG.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.stereotype.Component;

/**
 * Adapter — adapta o ObjectMapper do Jackson para a interface CharacterExporter.
 *
 * O ObjectMapper tem sua própria API (writeValueAsString, writerWithDefaultPrettyPrinter etc.)
 * que é incompatível com o que o sistema espera (export / getFormat).
 * Este Adapter faz a ponte entre os dois.
 */
@Component
public class JsonExporterAdapter implements CharacterExporter {

    // Classe "incompatível" sendo adaptada
    private final ObjectMapper objectMapper;

    public JsonExporterAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String export(CharacterSheet sheet) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(sheet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao exportar ficha para JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFormat() {
        return "json";
    }
}
