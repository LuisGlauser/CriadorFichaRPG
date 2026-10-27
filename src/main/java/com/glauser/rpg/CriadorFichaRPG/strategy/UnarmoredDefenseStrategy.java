package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class UnarmoredDefenseStrategy implements ArmorClassStrategy {

    @Override
    public int calculate(CharacterSheet character) {
        int dex = character.getAttributes().getModifier(
                character.getAttributes().getDexterity());
        int wis = character.getAttributes().getModifier(
                character.getAttributes().getWisdom());

        String classId = character.getCharacterClass() != null
                ? character.getCharacterClass().getId()
                : "";

        // Bárbaro: 10 + DES + CON
        if ("barbarian".equalsIgnoreCase(classId)) {
            int con = character.getAttributes().getModifier(
                    character.getAttributes().getConstitution());
            return 10 + dex + con + character.getShieldBonus();
        }

        // Monge: 10 + DES + SAB
        if ("monk".equalsIgnoreCase(classId)) {
            return 10 + dex + wis + character.getShieldBonus();
        }

        return 10 + dex + character.getShieldBonus();
    }
}
