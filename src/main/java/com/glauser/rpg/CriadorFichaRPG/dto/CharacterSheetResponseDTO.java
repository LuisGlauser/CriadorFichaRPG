package com.glauser.rpg.CriadorFichaRPG.dto;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public record CharacterSheetResponseDTO(
        String id,
        String name,
        int level,
        int currentHp,
        int maxHp,
        int armorClass,
        int temporaryHp,
        int strength,
        int dexterity,
        int constitution,
        int intelligence,
        int wisdom,
        int charisma,
        String notes,
        String armorType,
        String lifeState
) {

    public static CharacterSheetResponseDTO from(
            CharacterSheet c) {

        var a = c.getAttributes();

        return new CharacterSheetResponseDTO(
                c.getId(),
                c.getName(),
                c.getLevel(),
                c.getCurrentHp(),
                c.getMaxHp(),
                c.getArmorClass(),
                c.getTemporaryHp(),

                a.getStrength(),
                a.getDexterity(),
                a.getConstitution(),
                a.getIntelligence(),
                a.getWisdom(),
                a.getCharisma(),

                c.getNotes(),
                c.getArmorType(),
                c.getLifeStateName()
        );
    }
}
