package com.glauser.rpg.CriadorFichaRPG.builder;

import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public class CharacterBuilder {

    private String name;
    private int level;
    private CharacterClass characterClass;
    private Backgrounds background;
    private Attributes attributes;
    private Species species;

    public CharacterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public CharacterBuilder setClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
        return this;
    }

    public CharacterBuilder setSpecies(Species species) {
        this.species = species;
        return this;
    }

    public CharacterBuilder setBackground(Backgrounds background) {
        this.background = background;
        return this;
    }

    public CharacterBuilder setAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public CharacterSheet build() {
        CharacterSheet c = new CharacterSheet();

        c.setName(name);
        c.setLevel(level);
        c.setAttributes(attributes);
        c.setCharacterClass(characterClass);
        c.setSpecies(species);
        c.setBackground(background);

        c.setFeatures(mergeFeatures());
        c.setMaxHp(calculateHp());
        c.setCurrentHp(c.getMaxHp());

        return c;
    }

    private List<Features> mergeFeatures() {
        List<Features> all = new ArrayList<>();

        if (characterClass != null) {
            for (var entry : characterClass.getFeatures().entrySet()) {

                Features f = entry.getValue();

                if ((f.getLevel() != null && f.getLevel() <= level) ||
                        (f.getLevels() != null && f.getLevels().contains(level))) {

                    // 🔥 garante nome
                    if (f.getName() == null) {
                        f.setName(entry.getKey()); // usa a key do JSON
                    }

                    all.add(f);
                }
            }
        }

        return all;
    }

    private int calculateHp() {
        return characterClass.getHitDie() + attributes.getModifier(attributes.getConstitution());
    }
}