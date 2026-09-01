package com.glauser.rpg.CriadorFichaRPG.model.character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import com.glauser.rpg.CriadorFichaRPG.observer.CharacterObserver;
import com.glauser.rpg.CriadorFichaRPG.observer.CharacterSubject;
import com.glauser.rpg.CriadorFichaRPG.state.BleedingState;
import com.glauser.rpg.CriadorFichaRPG.state.LifeState;
import com.glauser.rpg.CriadorFichaRPG.state.NormalState;
import com.glauser.rpg.CriadorFichaRPG.state.UnconsciousState;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private LocalDateTime updatedAt;

    @JsonIgnore
    private transient LifeState lifeState;

    @JsonIgnore
    private final List<CharacterObserver> observers =
            new CopyOnWriteArrayList<>();

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
        updatedAt = LocalDateTime.now();

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
            case "strength" ->
                    attributes.setStrength(value);

            case "dexterity" ->
                    attributes.setDexterity(value);

            case "constitution" ->
                    attributes.setConstitution(value);

            case "intelligence" ->
                    attributes.setIntelligence(value);

            case "wisdom" ->
                    attributes.setWisdom(value);

            case "charisma" ->
                    attributes.setCharisma(value);

            default ->
                    throw new IllegalArgumentException(
                            "Atributo inválido: " + attribute
                    );
        }

        changed();
    }

    public void setDerivedValues(int maxHp, int armorClass) {
        this.maxHp = maxHp;
        this.armorClass = armorClass;

        updateLifeState();
    }

    /**
     * Altera HP sem disparar os observers.
     * Usado internamente quando o máximo de HP é recalculado.
     */
    public void setCurrentHpSilently(int currentHp) {
        this.currentHp = Math.max(0, currentHp);
        updateLifeState();
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

    /**
     * Setter usado pelo Jackson e pela edição manual.
     */
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, currentHp);
        updateLifeState();
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
        this.armorType =
                armorType == null ? "UNARMORED" : armorType;

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
            int absorbed =
                    Math.min(temporaryHp, Math.max(0, dmg));

            temporaryHp -= absorbed;
            dmg -= absorbed;
        }

        currentHp =
                Math.max(0, currentHp - Math.max(0, dmg));

        updateLifeState();
        changed();
    }

    @Override
    public void heal(int value) {

        int amount = Math.max(0, value);

        currentHp =
                Math.min(maxHp, currentHp + amount);

        updateLifeState();
        changed();
    }

    /**
     * Executa a cura de acordo com o estado atual.
     *
     * Este é o ponto principal da prova de conceito do State.
     */
    public void healByCurrentState() {

        updateLifeState();

        if (lifeState != null) {
            lifeState.heal(this);
        }
    }

    /**
     * Retorna o nome do estado atual.
     */
    public String getLifeStateName() {

        updateLifeState();

        return lifeState != null
                ? lifeState.getName()
                : "Inconsciente / Caído";
    }

    /**
     * Reconstrói o estado com base no HP atual.
     *
     * Normal:
     *     HP > 50%
     *
     * Sangrando:
     *     HP <= 50% e HP > 0
     *
     * Inconsciente:
     *     HP == 0
     */
    public void updateLifeState() {

        if (maxHp <= 0) {
            lifeState = new UnconsciousState();
            return;
        }

        if (currentHp <= 0) {
            lifeState = new UnconsciousState();

        } else if (currentHp * 2 <= maxHp) {
            lifeState = new BleedingState();

        } else {
            lifeState = new NormalState();
        }
    }
}