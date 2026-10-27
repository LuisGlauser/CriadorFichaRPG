package com.glauser.rpg.CriadorFichaRPG.command;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;

/**
 * Command
 * -------
 * Encapsula uma ação (request) que será executada sobre uma
 * {@link CharacterSheet} (o "receiver"), permitindo:
 *
 *  - Desacoplar quem pede a ação (controller) de quem sabe
 *    executá-la (a própria ficha / regras de negócio);
 *  - Guardar um histórico de ações executadas;
 *  - Desfazer (undo) uma ação já aplicada.
 *
 * Cada implementação concreta representa UMA ação de combate
 * (tomar dano, curar, ajustar HP temporário, etc).
 *
 * Observação de design: o comando NÃO guarda a referência da
 * CharacterSheet internamente. Como a ficha é recarregada do
 * disco a cada requisição (CharacterPersistenceService), o
 * receiver é passado como parâmetro em execute()/undo(). Isso
 * evita trabalhar com uma instância "velha" quando o undo
 * acontece em uma requisição HTTP diferente da que executou
 * o comando original.
 */
public interface CharacterCommand {

    /**
     * Aplica a ação sobre a ficha, guardando internamente o que
     * for necessário para permitir o undo() depois.
     */
    void execute(CharacterSheet sheet);

    /**
     * Reverte o efeito de execute() sobre a ficha informada.
     */
    void undo(CharacterSheet sheet);

    /**
     * Descrição curta da ação, usada em log/histórico exibido ao usuário.
     */
    String getDescription();
}
