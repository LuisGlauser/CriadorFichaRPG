package com.glauser.rpg.CriadorFichaRPG.dto;

import lombok.Data;

@Data
public class CharacterFormDTO {
    private String name;
    private int level;
    private String className;
    private String speciesName;
    private String backgroundName;

    // getters/setters
}
