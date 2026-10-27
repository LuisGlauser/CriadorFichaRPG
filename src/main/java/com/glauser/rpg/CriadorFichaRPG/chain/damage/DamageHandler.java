package com.glauser.rpg.CriadorFichaRPG.chain.damage;

/**
 * Handler do padrão Chain of Responsibility para dano.
 */
public interface DamageHandler {

    /**
     * Define o próximo elo e o retorna, permitindo encadear:
     * a.setNext(b).setNext(c);
     */
    DamageHandler setNext(DamageHandler next);

    void handle(DamageContext context);
}
