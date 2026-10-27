package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class HeavyArmorStrategy implements ArmorClassStrategy {
    @Override
    public int calculate(CharacterSheet character) {
        return character.getArmorBase() + character.getShieldBonus();
    }
}
