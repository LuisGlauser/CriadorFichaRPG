package com.glauser.rpg.CriadorFichaRPG.state;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public class BleedingState implements LifeState {

    @Override
    public void heal(CharacterSheet character) {
        character.heal(20);
    }

    @Override
    public String getName() {
        return "Sangrando";
    }
}
