package com.glauser.rpg.CriadorFichaRPG.dto;

public record CharacterPatchDTO(
        String name,
        Integer level,
        Integer currentHp,
        Integer temporaryHp,
        String notes,
        String armorType,
        Integer armorBase,
        Integer shieldBonus,
        Integer strength,
        Integer dexterity,
        Integer constitution,
        Integer intelligence,
        Integer wisdom,
        Integer charisma
) {}
