package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;

public class LevelValidator extends ValidationHandler {

    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 20;

    @Override
    protected void check(CharacterCreationDTO dto) {
        int level = dto.getLevel();

        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new ValidationException(
                    "O nível deve estar entre " + MIN_LEVEL + " e " + MAX_LEVEL + ".");
        }
    }
}
