package com.glauser.rpg.CriadorFichaRPG.decorator;

import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;

import java.util.List;

public abstract class CharacterSheetDecorator implements CharacterComponent {

    protected final CharacterComponent wrapped;

    public CharacterSheetDecorator(CharacterComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override public String getName()                   { return wrapped.getName(); }
    @Override public Attributes getAttributes()         { return wrapped.getAttributes(); }
    @Override public int getLevel()                     { return wrapped.getLevel(); }
    @Override public CharacterClass getCharacterClass() { return wrapped.getCharacterClass(); }
    @Override public Species getSpecies()               { return wrapped.getSpecies(); }
    @Override public Backgrounds getBackground()        { return wrapped.getBackground(); }
    @Override public int getMaxHp()                     { return wrapped.getMaxHp(); }
    @Override public int getCurrentHp()                 { return wrapped.getCurrentHp(); }
    @Override public int getTemporaryHp()               { return wrapped.getTemporaryHp(); }
    @Override public List<Features> getFeatures()       { return wrapped.getFeatures(); }
    @Override public String getActiveEffects()          { return wrapped.getActiveEffects(); }
    @Override public void takeDamage(int dmg)           { wrapped.takeDamage(dmg); }
    @Override public void heal(int value)               { wrapped.heal(value); }
}
