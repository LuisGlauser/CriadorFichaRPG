package com.glauser.rpg.CriadorFichaRPG.chain.validation;

/**
 * Lançada por um elo da cadeia quando a criação do personagem é inválida.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
