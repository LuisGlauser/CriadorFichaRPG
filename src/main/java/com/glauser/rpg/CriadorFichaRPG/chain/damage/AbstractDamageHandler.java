package com.glauser.rpg.CriadorFichaRPG.chain.damage;

/**
 * Implementação base: executa a própria regra e, se ainda sobrar dano,
 * repassa o contexto ao próximo elo.
 */
public abstract class AbstractDamageHandler implements DamageHandler {

    private DamageHandler next;

    @Override
    public DamageHandler setNext(DamageHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public void handle(DamageContext context) {
        process(context);

        if (next != null && !context.isFullyAbsorbed()) {
            next.handle(context);
        }
    }

    /** Regra específica deste elo da cadeia. */
    protected abstract void process(DamageContext context);
}
