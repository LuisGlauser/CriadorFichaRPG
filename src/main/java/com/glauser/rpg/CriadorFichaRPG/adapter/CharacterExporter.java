package com.glauser.rpg.CriadorFichaRPG.adapter;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

public interface CharacterExporter {
    String export(CharacterSheet sheet);

    String getFormat();
}
