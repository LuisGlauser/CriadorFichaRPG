package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;

public class NameValidator extends ValidationHandler {

    private static final int MAX_LENGTH = 50;

    @Override
    protected void check(CharacterCreationDTO dto) {
        String name = dto.getName();

        if (name == null || name.isBlank()) {
            throw new ValidationException("O nome do personagem é obrigatório.");
        }

        if (name.trim().length() > MAX_LENGTH) {
            throw new ValidationException(
                    "O nome deve ter no máximo " + MAX_LENGTH + " caracteres.");
        }
    }
}
