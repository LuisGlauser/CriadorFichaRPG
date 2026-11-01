package com.glauser.rpg.CriadorFichaRPG.rest;

public enum RestType {

    SHORT("Descanso curto"),
    LONG("Descanso longo");

    private final String label;

    RestType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
