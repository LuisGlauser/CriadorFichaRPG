package com.glauser.rpg.CriadorFichaRPG.command;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Invoker do padrão Command.
 *
 * Mantém, em memória, uma pilha de comandos já executados por
 * personagem (por id), para que seja possível desfazer a última
 * ação de combate (dano, cura, ajuste de HP temporário).
 *
 * É um @Component singleton porque o histórico de undo é um
 * detalhe de aplicação (não é salvo no JSON da ficha).
 */
@Component
public class CharacterCommandInvoker {

    private final Map<String, Deque<CharacterCommand>> history =
            new ConcurrentHashMap<>();

    /**
     * Executa o comando sobre a ficha e o empilha no histórico
     * daquele personagem.
     */
    public void execute(String characterId, CharacterCommand command, CharacterSheet sheet) {
        command.execute(sheet);

        history
                .computeIfAbsent(characterId, id -> new ArrayDeque<>())
                .push(command);
    }

    /**
     * Desfaz o último comando executado para o personagem informado.
     *
     * @return true se havia um comando para desfazer, false caso contrário.
     */
    public boolean undoLast(String characterId, CharacterSheet sheet) {

        Deque<CharacterCommand> stack = history.get(characterId);

        if (stack == null || stack.isEmpty()) {
            return false;
        }

        CharacterCommand last = stack.pop();
        last.undo(sheet);

        return true;
    }

    public boolean hasHistory(String characterId) {
        Deque<CharacterCommand> stack = history.get(characterId);
        return stack != null && !stack.isEmpty();
    }

    /**
     * Limpa o histórico de um personagem (ex: se a ficha for excluída).
     */
    public void clear(String characterId) {
        history.remove(characterId);
    }
}
