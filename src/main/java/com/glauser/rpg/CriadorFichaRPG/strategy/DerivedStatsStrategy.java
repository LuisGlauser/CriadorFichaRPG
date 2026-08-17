package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public interface DerivedStatsStrategy {
    DerivedStats calculate(CharacterSheet character);
}
