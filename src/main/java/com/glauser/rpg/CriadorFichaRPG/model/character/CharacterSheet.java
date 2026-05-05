package com.glauser.rpg.CriadorFichaRPG.model.character;

import java.util.List;

import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import lombok.Data;

@Data
public class CharacterSheet {

    private String name;
    private Attributes attributes;
    private int level;

    private CharacterClass characterClass;
    private Species species;
    private Backgrounds background;

    private int maxHp;
    private int currentHp;

    private List<Features> features;

    public void takeDamage(int dmg) {
        this.currentHp -= dmg;
    }

    public void heal(int value) {
        this.currentHp += value;
    }

}
