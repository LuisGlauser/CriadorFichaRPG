package com.glauser.rpg.CriadorFichaRPG.command;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Comando concreto que aplica dano à ficha.
 *
 * A regra de negócio (tirar primeiro do HP temporário e só depois
 * do HP atual) já existe em {@link CharacterSheet#takeDamage(int)};
 * este comando apenas a encapsula como uma ação de primeira classe,
 * guardando o estado anterior para permitir desfazer.
 */
public class TakeDamageCommand implements CharacterCommand {

    private final int amount;

    // Estado anterior, capturado em execute() e usado em undo()
    private int previousCurrentHp;
    private int previousTemporaryHp;

    public TakeDamageCommand(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public void execute(CharacterSheet sheet) {
        previousCurrentHp = sheet.getCurrentHp();
        previousTemporaryHp = sheet.getTemporaryHp();

        // Regra "tira do temporário antes do atual" já está aqui dentro
        sheet.takeDamage(amount);
    }

    @Override
    public void undo(CharacterSheet sheet) {
        sheet.setTemporaryHp(previousTemporaryHp);
        sheet.setCurrentHp(previousCurrentHp);
    }

    @Override
    public String getDescription() {
        return "Sofreu " + amount + " de dano";
    }
}
