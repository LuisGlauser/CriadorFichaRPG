package com.glauser.rpg.CriadorFichaRPG.model.character;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attributes {

    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;

    public Attributes(int strength, int dexterity, int constitution,
                      int intelligence, int wisdom, int charisma) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    public int getModifier(int value) {
        return (int) Math.floor((value - 10.0) / 2.0);
    }

    public int getConModifier() {
        return getModifier(constitution);
    }
}