package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttributesValidator extends ValidationHandler {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 20;

    @Override
    protected void check(CharacterCreationDTO dto) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Força", dto.getStrength());
        scores.put("Destreza", dto.getDexterity());
        scores.put("Constituição", dto.getConstitution());
        scores.put("Inteligência", dto.getIntelligence());
        scores.put("Sabedoria", dto.getWisdom());
        scores.put("Carisma", dto.getCharisma());

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            int value = entry.getValue();

            if (value < MIN_SCORE || value > MAX_SCORE) {
                throw new ValidationException(
                        entry.getKey() + " deve estar entre "
                                + MIN_SCORE + " e " + MAX_SCORE + " (recebido: " + value + ").");
            }
        }
    }
}
