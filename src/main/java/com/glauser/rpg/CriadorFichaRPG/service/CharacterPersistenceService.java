package com.glauser.rpg.CriadorFichaRPG.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CharacterPersistenceService {

    private static final Path SAVE_DIRECTORY =
            Paths.get("saves", "characters");

    private final ObjectMapper objectMapper;

    public CharacterPersistenceService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void ensureDirectory() throws IOException {
        Files.createDirectories(SAVE_DIRECTORY);
    }

    private Path getFile(String id) {
        return SAVE_DIRECTORY.resolve(id + ".json");
    }

    /**
     * Salvamento assíncrono utilizado pelo Observer.
     */
    @Async("characterSaveExecutor")
    public void saveAsync(CharacterSheet character) {
        save(character);
    }

    /**
     * Salva a ficha inteira em JSON.
     */
    public CharacterSheet save(CharacterSheet character) {

        try {
            if (character.getId() == null ||
                    character.getId().isBlank()) {

                throw new IllegalArgumentException(
                        "A ficha precisa ter um ID antes de ser salva."
                );
            }

            ensureDirectory();

            character.updateLifeState();

            Path target = getFile(character.getId());
            Path temp = SAVE_DIRECTORY.resolve(
                    character.getId() + ".tmp"
            );

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(temp.toFile(), character);

            /*
             * Escrita atômica:
             * primeiro salva .tmp e depois substitui o JSON.
             */
            try {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return character;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Não foi possível salvar a ficha em JSON.",
                    e
            );
        }
    }

    /**
     * Lê uma ficha pelo ID.
     */
    public CharacterSheet findById(String id) {

        try {
            ensureDirectory();

            Path file = getFile(id);

            if (!Files.exists(file)) {
                return null;
            }

            CharacterSheet character =
                    objectMapper.readValue(
                            file.toFile(),
                            CharacterSheet.class
                    );

            /*
             * O State é reconstruído após o JSON ser carregado.
             */
            character.updateLifeState();

            return character;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Não foi possível ler a ficha " + id,
                    e
            );
        }
    }

    /**
     * Retorna todas as fichas salvas.
     */
    public List<CharacterSheet> findAll() {

        try {
            ensureDirectory();

            List<CharacterSheet> characters =
                    new ArrayList<>();

            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(
                                 SAVE_DIRECTORY,
                                 "*.json"
                         )) {

                for (Path file : stream) {

                    try {

                        CharacterSheet character =
                                objectMapper.readValue(
                                        file.toFile(),
                                        CharacterSheet.class
                                );

                        character.updateLifeState();

                        characters.add(character);

                    } catch (Exception ignored) {
                        /*
                         * Um JSON inválido não impede que
                         * as demais fichas sejam carregadas.
                         */
                    }
                }
            }

            characters.sort(
                    Comparator.comparing(
                            CharacterSheet::getName,
                            Comparator.nullsLast(
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    )
            );

            return characters;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Não foi possível listar as fichas salvas.",
                    e
            );
        }
    }
}
