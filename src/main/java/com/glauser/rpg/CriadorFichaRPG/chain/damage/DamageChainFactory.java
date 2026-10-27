package com.glauser.rpg.CriadorFichaRPG.chain.damage;

/**
 * Monta a cadeia de dano.
 *
 * Para incluir uma nova regra (ex: resistência), basta criar um novo
 * handler e adicioná-lo aqui — nenhum handler existente muda.
 */
public final class DamageChainFactory {

    private DamageChainFactory() {
    }

    public static DamageHandler createDefault() {
        DamageHandler first = new TemporaryHpHandler();

        first.setNext(new CurrentHpHandler());

        return first;
    }
}
