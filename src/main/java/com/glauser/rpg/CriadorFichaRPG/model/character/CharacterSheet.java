package com.glauser.rpg.CriadorFichaRPG.model.character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import com.glauser.rpg.CriadorFichaRPG.observer.CharacterObserver;
import com.glauser.rpg.CriadorFichaRPG.observer.CharacterSubject;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Data
public class CharacterSheet implements CharacterComponent, CharacterSubject {

    private String id;
    private String name;
    private Attributes attributes;
    private int level;

    private CharacterClass characterClass;
    private Species species;
    private Backgrounds background;

    private int maxHp;
    private int currentHp;
    private int armorClass;

    private int temporaryHp = 0;
    private String notes = "";

    // UNARMORED, LIGHT, MEDIUM ou HEAVY
    private String armorType = "UNARMORED";
    private int armorBase = 10;
    private int shieldBonus = 0;

    private List<Features> features;

    @JsonIgnore
    private final List<CharacterObserver> observers = new CopyOnWriteArrayList<>();

    @JsonIgnore
    private transient int updateDepth = 0;

    @JsonIgnore
    private transient boolean dirty = false;

    @Override
    public void addObserver(CharacterObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(CharacterObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (CharacterObserver observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Agrupa várias alterações em uma única notificação.
     * Isso evita seis chamadas de auto-save ao editar os seis atributos.
     */
    public void updateBatch(Runnable changes) {
        updateDepth++;
        try {
            changes.run();
        } finally {
            updateDepth--;
            if (updateDepth == 0 && dirty) {
                dirty = false;
                notifyObservers();
            }
        }
    }

    private void changed() {
        if (updateDepth > 0) {
            dirty = true;
        } else {
            notifyObservers();
        }
    }

    public void updateAttribute(String attribute, int value) {
        if (attributes == null) {
            return;
        }

        switch (attribute.toLowerCase()) {
            case "strength" -> attributes.setStrength(value);
            case "dexterity" -> attributes.setDexterity(value);
            case "constitution" -> attributes.setConstitution(value);
            case "intelligence" -> attributes.setIntelligence(value);
            case "wisdom" -> attributes.setWisdom(value);
            case "charisma" -> attributes.setCharisma(value);
            default -> throw new IllegalArgumentException("Atributo inválido: " + attribute);
        }

        changed();
    }

    public void setDerivedValues(int maxHp, int armorClass) {
        this.maxHp = maxHp;
        this.armorClass = armorClass;
    }

    public void setCurrentHpSilently(int currentHp) {
        this.currentHp = currentHp;
    }

    public void setName(String name) {
        this.name = name;
        changed();
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
        changed();
    }

    public void setLevel(int level) {
        this.level = level;
        changed();
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
        changed();
    }

    public void setSpecies(Species species) {
        this.species = species;
        changed();
    }

    public void setBackground(Backgrounds background) {
        this.background = background;
        changed();
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
        changed();
    }

    public void setTemporaryHp(int temporaryHp) {
        this.temporaryHp = Math.max(0, temporaryHp);
        changed();
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
        changed();
    }

    public void setArmorType(String armorType) {
        this.armorType = armorType == null ? "UNARMORED" : armorType;
        changed();
    }

    public void setArmorBase(int armorBase) {
        this.armorBase = armorBase;
        changed();
    }

    public void setShieldBonus(int shieldBonus) {
        this.shieldBonus = shieldBonus;
        changed();
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
        changed();
    }

    @Override
    public String getActiveEffects() {
        return "Nenhum efeito ativo";
    }

    @Override
    public void takeDamage(int dmg) {
        if (temporaryHp > 0) {
            int absorbed = Math.min(temporaryHp, Math.max(0, dmg));
            temporaryHp -= absorbed;
            dmg -= absorbed;
        }
        currentHp = Math.max(0, currentHp - Math.max(0, dmg));
        changed();
    }

    @Override
    public void heal(int value) {
        currentHp = Math.min(maxHp, currentHp + Math.max(0, value));
        changed();
    }
}
