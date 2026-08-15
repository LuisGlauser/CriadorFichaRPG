package com.glauser.rpg.CriadorFichaRPG.model.character;

import java.util.List;

import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import lombok.Data;

@Data
public class CharacterSheet implements CharacterComponent {

    private String name;
    private Attributes attributes;
    private int level;

    private CharacterClass characterClass;
    private Species species;
    private Backgrounds background;

    private int maxHp;
    private int currentHp;

    private int temporaryHp = 0;

    private List<Features> features;

    @Override
    public String getActiveEffects() {
        return "Nenhum efeito ativo";
    }

    @Override
    public void takeDamage(int dmg) {
        this.currentHp -= dmg;
    }

    @Override
    public void heal(int value) {
        this.currentHp += value;
    }
}
