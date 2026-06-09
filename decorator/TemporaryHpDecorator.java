package com.glauser.rpg.CriadorFichaRPG.decorator;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;

/**
 * Decorator: adiciona Vida Temporária ao personagem.
 *
 * Regras (fiéis ao D&D 5e):
 *  - HP temporário NÃO se soma ao HP atual — é um buffer separado.
 *  - Dano consome primeiro o temporário; só o excedente vai ao HP real.
 *  - Cura não restaura HP temporário.
 *  - getTemporaryHp() retorna o valor atual para a view exibir.
 */
public class TemporaryHpDecorator extends CharacterSheetDecorator {

    private int temporaryHp;

    public TemporaryHpDecorator(CharacterComponent wrapped, int temporaryHp) {
        super(wrapped);
        this.temporaryHp = temporaryHp;
    }

    /** Exibido na ficha como campo separado — NÃO some ao currentHp. */
    @Override
    public int getTemporaryHp() {
        return temporaryHp;
    }

    /** Dano drena o buffer temporário primeiro. */
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
