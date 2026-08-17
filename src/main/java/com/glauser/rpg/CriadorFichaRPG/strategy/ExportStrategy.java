package com.glauser.rpg.CriadorFichaRPG.strategy;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public interface ExportStrategy {
    String export(CharacterSheet sheet);
    String getFormat();
}
