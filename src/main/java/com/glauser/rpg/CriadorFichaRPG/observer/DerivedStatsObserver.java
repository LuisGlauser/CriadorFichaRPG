package com.glauser.rpg.CriadorFichaRPG.observer;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.strategy.DerivedStats;
import com.glauser.rpg.CriadorFichaRPG.strategy.DerivedStatsStrategy;
import org.springframework.stereotype.Component;

@Component
public class DerivedStatsObserver implements CharacterObserver {

    private final DerivedStatsStrategy strategy;

    public DerivedStatsObserver(
            DerivedStatsStrategy strategy) {

        this.strategy = strategy;
    }

    @Override
    public void update(CharacterSheet character) {

        DerivedStats stats =
                strategy.calculate(character);

        int oldMaxHp =
                character.getMaxHp();

        character.setDerivedValues(
                stats.maxHp(),
                stats.armorClass()
        );

        /*
         * Não cura o personagem ao aumentar o HP máximo.
         *
         * Se o novo máximo ficar abaixo do HP atual,
         * apenas reduzimos o HP atual.
         */
        if (oldMaxHp > 0 &&
                character.getCurrentHp() > stats.maxHp()) {

            character.setCurrentHpSilently(
                    stats.maxHp()
            );
        }

        character.updateLifeState();
    }
}
