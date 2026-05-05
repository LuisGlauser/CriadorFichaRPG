package com.glauser.rpg.CriadorFichaRPG.model.content;

import lombok.Data;

import java.util.List;

@Data
public class SkillProficiency {
    private int choose;
    private List<String> options;
}