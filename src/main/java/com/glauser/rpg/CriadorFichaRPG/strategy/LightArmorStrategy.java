package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class LightArmorStrategy implements ArmorClassStrategy {
    @Override
    public int calculate(CharacterSheet character) {
        int dex = character.getAttributes().getModifier(
                character.getAttributes().getDexterity());
        return character.getArmorBase() + dex + character.getShieldBonus();
    }
}
