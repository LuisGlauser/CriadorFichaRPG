package com.glauser.rpg.CriadorFichaRPG.model.content;

import lombok.Data;

import java.util.List;

@Data
public class StartingEquipment {
    private List<EquipmentOption> options;
}