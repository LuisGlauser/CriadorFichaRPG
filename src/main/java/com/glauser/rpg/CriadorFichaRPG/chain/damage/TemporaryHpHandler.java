package com.glauser.rpg.CriadorFichaRPG.chain.damage;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Primeiro elo: o HP temporário absorve o dano antes do HP real (D&D 5e).
 */
public class TemporaryHpHandler extends AbstractDamageHandler {

    @Override
    protected void process(DamageContext context) {
        CharacterSheet sheet = context.getSheet();
        int temporaryHp = sheet.getTemporaryHp();

        if (temporaryHp <= 0 || context.isFullyAbsorbed()) {
            return;
        }

        int absorbed = Math.min(temporaryHp, context.getRemaining());

        sheet.setTemporaryHp(temporaryHp - absorbed);
        context.absorb(absorbed);
        context.addLog("HP temporário absorveu " + absorbed + " de dano");
    }
}
