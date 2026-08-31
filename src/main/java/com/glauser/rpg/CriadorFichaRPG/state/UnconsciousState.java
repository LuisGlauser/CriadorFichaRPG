package com.glauser.rpg.CriadorFichaRPG.state;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class UnconsciousState implements LifeState {

    @Override
    public void heal(CharacterSheet character) {
        character.heal(30);
    }

    @Override
    public String getName() {
        return "Inconsciente / Caído";
    }
}
