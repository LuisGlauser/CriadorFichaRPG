package com.glauser.rpg.CriadorFichaRPG.adapter;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Interface alvo do padrão Adapter.
 *
 * Define o contrato único que o sistema conhece para exportar fichas.
 * Cada formato externo (JSON, CSV, TXT) é adaptado para esta interface,
 * independente de como cada biblioteca ou mecanismo funciona internamente.
 */
public interface CharacterExporter {

    /**
     * Exporta a ficha para uma String no formato correspondente.
     */
    String export(CharacterSheet sheet);

    /**
     * Retorna o identificador do formato (ex: "json", "csv", "txt").
     * Usado pelo controller para selecionar o exporter correto.
     */
    String getFormat();
}
