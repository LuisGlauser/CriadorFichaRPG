package com.glauser.rpg.CriadorFichaRPG.decorator;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;


public class TemporaryHpDecorator extends CharacterSheetDecorator {

    private int temporaryHp;

    public TemporaryHpDecorator(CharacterComponent wrapped, int temporaryHp) {
        super(wrapped);
        this.temporaryHp = temporaryHp;
    }

    @Override
    public int getTemporaryHp() {
        return temporaryHp;
    }

    @Override
    public void takeDamage(int dmg) {
        if (temporaryHp >= dmg) {
            temporaryHp -= dmg;
        } else {
            int resto = dmg - temporaryHp;
            temporaryHp = 0;
            wrapped.takeDamage(resto);
        }
    }

    @Override
    public String getActiveEffects() {
        return wrapped.getActiveEffects()
            + " | Vida Temporária: " + temporaryHp + " HP";
    }
}
