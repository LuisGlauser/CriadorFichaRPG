package com.glauser.rpg.CriadorFichaRPG.mediator;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.decorator.ItemBoostDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.TemporaryHpDecorator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.stereotype.Component;

/**
 * Mediator responsável por coordenar a aplicação dos efeitos temporários
 * e dos equipamentos sobre a representação de uma ficha.
 *
 * O mediador não implementa as regras dos Decorators: apenas decide quais
 * deles devem compor o CharacterComponent de acordo com o EffectsState.
 */
@Component
public class CharacterMediator {

    private static final String GLOVES_NAME = "Gauntlets of Ogre Power";
    private static final String GLOVES_ATTRIBUTE = "strength";
    private static final int GLOVES_STRENGTH = 19;

    /**
     * Coordena uma alteração de efeito. O estado continua pertencendo à sessão
     * HTTP; este método apenas centraliza as operações permitidas.
     */
    public void handleEffect(EffectsState effects, String action, Integer temporaryHp) {
        if (effects == null) {
            throw new IllegalArgumentException("O estado dos efeitos não pode ser nulo.");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Ação de efeito não informada.");
        }

        switch (action) {
            case "equip_gloves" -> effects.setGlovesEquipped(true);
            case "unequip_gloves" -> effects.setGlovesEquipped(false);
            case "set_temp_hp" -> effects.setTemporaryHp(temporaryHp == null ? 0 : temporaryHp);
            case "clear_temp_hp" -> effects.setTemporaryHp(0);
            case "clear_all" -> effects.removeAllEffects();
            default -> throw new IllegalArgumentException("Ação de efeito desconhecida: " + action);
        }
    }

    /**
     * Monta a composição de Decorators correspondente aos efeitos ativos.
     * A ficha original não é modificada: os Decorators apenas alteram a
     * representação CharacterComponent devolvida.
     */
    public CharacterComponent buildCharacter(CharacterSheet character, EffectsState effects) {
        if (character == null) {
            throw new IllegalArgumentException("A ficha do personagem não pode ser nula.");
        }
        if (effects == null) {
            return character;
        }

        CharacterComponent decoratedCharacter = character;

        if (effects.isGlovesEquipped()) {
            decoratedCharacter = new ItemBoostDecorator(
                    decoratedCharacter,
                    GLOVES_NAME,
                    GLOVES_ATTRIBUTE,
                    GLOVES_STRENGTH
            );
        }

        if (effects.getTemporaryHp() > 0) {
            decoratedCharacter = new TemporaryHpDecorator(
                    decoratedCharacter,
                    effects.getTemporaryHp()
            );
        }

        return decoratedCharacter;
    }
}
