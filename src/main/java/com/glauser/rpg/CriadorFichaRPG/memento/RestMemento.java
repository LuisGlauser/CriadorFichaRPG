package com.glauser.rpg.CriadorFichaRPG.memento;

import java.time.LocalDateTime;

/**
 * Memento (interface "estreita")
 * ------------------------------
 * É a única visão que o Caretaker ({@link RestHistory}) e o resto da
 * aplicação têm de um snapshot da ficha: só metadados.
 *
 * O ESTADO guardado (HP, HP temporário, dados de vida gastos e usos de
 * features) fica numa implementação privada dentro de CharacterSheet
 * (o Originator), que é o único que consegue ler e restaurar esse
 * conteúdo. Assim o encapsulamento do padrão é preservado.
 */
public interface RestMemento {

    /** Texto curto exibido ao usuário, ex.: "Antes do descanso longo". */
    String label();

    /** Momento em que o snapshot foi tirado. */
    LocalDateTime createdAt();
}
