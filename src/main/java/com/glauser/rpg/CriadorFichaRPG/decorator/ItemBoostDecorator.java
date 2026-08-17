package com.glauser.rpg.CriadorFichaRPG.decorator;

import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;

/**
 * Decorator: simula um item mágico que define um atributo a um valor fixo.
 * Ex: "Gauntlets of Ogre Power" → Força = 19 (independente do valor base).
 *
 * Não muta o objeto original — cria uma cópia dos atributos com o valor alterado.
 */
public class ItemBoostDecorator extends CharacterSheetDecorator {

    private final String itemName;
    private final String attribute;   // "strength", "dexterity", etc.
    private final int    fixedValue;  // valor que o item define

    public ItemBoostDecorator(CharacterComponent wrapped,
                              String itemName,
                              String attribute,
                              int fixedValue) {
        super(wrapped);
        this.itemName   = itemName;
        this.attribute  = attribute;
        this.fixedValue = fixedValue;
    }

    @Override
    public Attributes getAttributes() {
        Attributes original = wrapped.getAttributes();

        // Copia os valores atuais para não mutar o objeto base
        Attributes copy = new Attributes(
            original.getStrength(),
            original.getDexterity(),
            original.getConstitution(),
            original.getIntelligence(),
            original.getWisdom(),
            original.getCharisma()
        );

        // Aplica o valor fixo apenas no atributo do item
        switch (attribute.toLowerCase()) {
            case "strength"     -> copy.setStrength(fixedValue);
            case "dexterity"    -> copy.setDexterity(fixedValue);
            case "constitution" -> copy.setConstitution(fixedValue);
            case "intelligence" -> copy.setIntelligence(fixedValue);
            case "wisdom"       -> copy.setWisdom(fixedValue);
            case "charisma"     -> copy.setCharisma(fixedValue);
        }

        return copy;
    }

    @Override
    public String getActiveEffects() {
        return wrapped.getActiveEffects()
            + " | " + itemName + " → " + attribute + " = " + fixedValue;
    }
}
