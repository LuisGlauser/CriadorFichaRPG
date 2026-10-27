package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;

/**
 * Elo base da cadeia de validação.
 *
 * Cada elo faz a sua verificação em check(); se ela passar, o DTO segue
 * para o próximo elo. Se falhar, check() lança ValidationException e a
 * cadeia é interrompida.
 */
public abstract class ValidationHandler {

    private ValidationHandler next;

    /**
     * Define o próximo elo e o retorna, permitindo encadear:
     * a.setNext(b).setNext(c);
     */
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public void validate(CharacterCreationDTO dto) {
        check(dto);

        if (next != null) {
            next.validate(dto);
        }
    }

    /** Regra específica deste elo. Deve lançar ValidationException se inválido. */
    protected abstract void check(CharacterCreationDTO dto);
}
