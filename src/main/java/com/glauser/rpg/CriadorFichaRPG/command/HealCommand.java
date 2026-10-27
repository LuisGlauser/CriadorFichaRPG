package com.glauser.rpg.CriadorFichaRPG.command;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Comando concreto que cura o personagem de acordo com o
 * {@code LifeState} atual (delegando para healByCurrentState(),
 * já implementado como prova de conceito do padrão State).
 *
 * Antes esse fluxo era chamado direto pelo CharacterService;
 * agora ele também vira um Command, ganhando undo "de graça".
 */
public class HealCommand implements CharacterCommand {

    private int previousCurrentHp;

    @Override
    public void execute(CharacterSheet sheet) {
        previousCurrentHp = sheet.getCurrentHp();
        sheet.healByCurrentState();
    }

    @Override
    public void undo(CharacterSheet sheet) {
        sheet.setCurrentHp(previousCurrentHp);
    }

    @Override
    public String getDescription() {
        return "Curou (State: cura automática)";
    }
}
