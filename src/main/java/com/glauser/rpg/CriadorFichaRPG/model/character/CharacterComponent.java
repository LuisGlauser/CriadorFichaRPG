package com.glauser.rpg.CriadorFichaRPG.model.character;

import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import java.util.List;

public interface CharacterComponent {
    String getName();
    Attributes getAttributes();
    int getLevel();
    CharacterClass getCharacterClass();
    Species getSpecies();
    Backgrounds getBackground();
    int getMaxHp();
    int getCurrentHp();
    int getTemporaryHp();
    List<Features> getFeatures();
    String getActiveEffects();
    void takeDamage(int dmg);
    void heal(int value);
}
