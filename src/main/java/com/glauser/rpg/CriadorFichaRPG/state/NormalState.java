package com.glauser.rpg.CriadorFichaRPG.state;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class NormalState implements LifeState {

    @Override
    public void heal(CharacterSheet character) {
        character.heal(15);
    }

    @Override
    public String getName() {
        return "Normal";
    }
}
