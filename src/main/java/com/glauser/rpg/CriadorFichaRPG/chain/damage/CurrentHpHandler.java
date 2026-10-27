package com.glauser.rpg.CriadorFichaRPG.chain.damage;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Último elo: o dano que sobrou é descontado do HP atual (mínimo 0).
 */
public class CurrentHpHandler extends AbstractDamageHandler {

    @Override
    protected void process(DamageContext context) {
        CharacterSheet sheet = context.getSheet();
        int remaining = context.getRemaining();
        int currentHp = sheet.getCurrentHp();

        sheet.setCurrentHp(Math.max(0, currentHp - remaining));

        context.absorb(remaining);
        context.addLog("HP atual sofreu " + Math.min(currentHp, remaining) + " de dano");
    }
}
