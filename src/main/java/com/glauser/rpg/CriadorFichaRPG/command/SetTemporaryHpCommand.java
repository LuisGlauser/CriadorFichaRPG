package com.glauser.rpg.CriadorFichaRPG.command;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Comando concreto que define o HP temporário da ficha
 * (o "escudo" que o TakeDamageCommand consome antes do HP atual).
 */
public class SetTemporaryHpCommand implements CharacterCommand {

    private final int newValue;
    private int previousTemporaryHp;

    public SetTemporaryHpCommand(int newValue) {
        this.newValue = Math.max(0, newValue);
    }

    @Override
    public void execute(CharacterSheet sheet) {
        previousTemporaryHp = sheet.getTemporaryHp();
        sheet.setTemporaryHp(newValue);
    }

    @Override
    public void undo(CharacterSheet sheet) {
        sheet.setTemporaryHp(previousTemporaryHp);
    }

    @Override
    public String getDescription() {
        return "Definiu HP temporário para " + newValue;
    }
}
