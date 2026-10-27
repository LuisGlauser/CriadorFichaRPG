package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.stereotype.Component;

@Component
public class Dnd5eDerivedStatsStrategy implements DerivedStatsStrategy {

    private final ArmorClassStrategy unarmored = new UnarmoredDefenseStrategy();
    private final ArmorClassStrategy light = new LightArmorStrategy();
    private final ArmorClassStrategy medium = new MediumArmorStrategy();
    private final ArmorClassStrategy heavy = new HeavyArmorStrategy();

    @Override
    public DerivedStats calculate(CharacterSheet character) {
        int level = Math.max(1, character.getLevel());
        int hitDie = character.getCharacterClass() != null
                ? character.getCharacterClass().getHitDie() : 0;

        int conMod = character.getAttributes() != null
                ? character.getAttributes().getConModifier() : 0;

        int maxHp = hitDie > 0
                ? hitDie + conMod + (hitDie / 2 + 1 + conMod) * (level - 1)
                : 0;

        ArmorClassStrategy armorStrategy = switch (
                character.getArmorType() == null ? "UNARMORED" :
                character.getArmorType().toUpperCase()) {
            case "LIGHT" -> light;
            case "MEDIUM" -> medium;
            case "HEAVY" -> heavy;
            default -> unarmored;
        };

        return new DerivedStats(Math.max(1, maxHp),
                armorStrategy.calculate(character));
    }
}
