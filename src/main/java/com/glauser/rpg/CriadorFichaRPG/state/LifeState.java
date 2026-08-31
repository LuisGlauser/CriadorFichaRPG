package com.glauser.rpg.CriadorFichaRPG.state;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public interface LifeState {

    void heal(CharacterSheet character);

    String getName();
}


