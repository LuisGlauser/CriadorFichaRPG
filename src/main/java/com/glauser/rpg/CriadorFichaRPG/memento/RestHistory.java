package com.glauser.rpg.CriadorFichaRPG.memento;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caretaker do padrão Memento.
 *
 * Guarda, por personagem, os snapshots tirados ANTES de cada descanso.
 * Nunca olha dentro do memento: só empilha e devolve.
 *
 * Fica em memória, como o histórico do CharacterCommandInvoker: a ficha
 * é recarregada do JSON a cada requisição, então o snapshot precisa viver
 * fora da instância de CharacterSheet.
 */
@Component
public class RestHistory {

    /** Quantos descansos anteriores ficam disponíveis para restauração. */
    static final int MAX_PER_CHARACTER = 5;

    private final Map<String, Deque<RestMemento>> history =
            new ConcurrentHashMap<>();

    public void push(String characterId, RestMemento memento) {

        Deque<RestMemento> stack =
                history.computeIfAbsent(characterId, id -> new ArrayDeque<>());

        synchronized (stack) {
            stack.push(memento);

            while (stack.size() > MAX_PER_CHARACTER) {
                stack.removeLast(); // descarta o mais antigo
            }
        }
    }

    /** Remove e devolve o snapshot mais recente. */
    public Optional<RestMemento> pop(String characterId) {

        Deque<RestMemento> stack = history.get(characterId);

        if (stack == null) {
            return Optional.empty();
        }

        synchronized (stack) {
            return Optional.ofNullable(stack.poll());
        }
    }

    /** Consulta o snapshot mais recente sem removê-lo. */
    public Optional<RestMemento> peek(String characterId) {

        Deque<RestMemento> stack = history.get(characterId);

        if (stack == null) {
            return Optional.empty();
        }

        synchronized (stack) {
            return Optional.ofNullable(stack.peek());
        }
    }

    public void clear(String characterId) {
        history.remove(characterId);
    }
}
