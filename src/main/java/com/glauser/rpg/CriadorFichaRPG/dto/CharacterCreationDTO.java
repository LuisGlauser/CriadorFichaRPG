package com.glauser.rpg.CriadorFichaRPG.dto;

import lombok.Data;

@Data
public class CharacterCreationDTO {

    // STEP 1
    private String name;
    private int level;

    // STEP 2 (escolhas do usuário)
    private String speciesId;
    private String backgroundId;

    // STEP 3
    private String classId;

    // STEP 6
    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;

}
