package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class MediumArmorStrategy implements ArmorClassStrategy {
    @Override
    public int calculate(CharacterSheet character) {
        int dex = Math.min(2, character.getAttributes().getModifier(
                character.getAttributes().getDexterity()));
        return character.getArmorBase() + dex + character.getShieldBonus();
    }
}
