package com.glauser.rpg.CriadorFichaRPG.decorator;

import java.io.Serializable;

/**
 * Armazena quais efeitos estão ativos para o personagem atual.
 * Fica na sessão HTTP — persiste entre requests sem banco de dados.
 */
public class EffectsState implements Serializable {

    private boolean glovesEquipped = false;   // Gauntlets of Ogre Power
    private int     temporaryHp    = 0;       // Vida temporária atual

    public boolean isGlovesEquipped()              { return glovesEquipped; }
    public void    setGlovesEquipped(boolean v)    { this.glovesEquipped = v; }

    public int  getTemporaryHp()                   { return temporaryHp; }
    public void setTemporaryHp(int v)              { this.temporaryHp = Math.max(0, v); }

    public void removeAllEffects() {
        this.glovesEquipped = false;
        this.temporaryHp    = 0;
    }
}
