package com.glauser.rpg.CriadorFichaRPG.chain.damage;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Objeto que viaja pela cadeia de dano.
 *
 * Guarda a ficha alvo, quanto dano ainda falta ser aplicado
 * e um log do que cada elo da cadeia fez.
 */
public class DamageContext {

    private final CharacterSheet sheet;
    private int remaining;
    private final List<String> log = new ArrayList<>();

    public DamageContext(CharacterSheet sheet, int amount) {
        this.sheet = sheet;
        this.remaining = Math.max(0, amount);
    }

    public CharacterSheet getSheet() {
        return sheet;
    }

    public int getRemaining() {
        return remaining;
    }

    /** Marca uma parte do dano como absorvida (não segue adiante na cadeia). */
    public void absorb(int amount) {
        this.remaining = Math.max(0, remaining - Math.max(0, amount));
    }

    public boolean isFullyAbsorbed() {
        return remaining <= 0;
    }

    public void addLog(String message) {
        log.add(message);
    }

    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }
}
