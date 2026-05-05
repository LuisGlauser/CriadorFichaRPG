package com.glauser.rpg.CriadorFichaRPG.model.content;

import lombok.Data;

import java.util.List;

@Data
public class EquipmentOption {
    private String id;
    private List<Object> items; // mistura string + gold
}
