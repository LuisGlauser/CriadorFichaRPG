package com.glauser.rpg.CriadorFichaRPG.model.content;

import lombok.Data;

import java.util.List;

@Data
public class Size {
    private List<SizeOption> options;
    private String selection_rule;
}
