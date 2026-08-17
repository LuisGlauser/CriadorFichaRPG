package com.glauser.rpg.CriadorFichaRPG.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

@Service
public class CharacterPersistenceService {

    private final ObjectMapper objectMapper;
    private final Path storageDirectory;

    public CharacterPersistenceService(
            ObjectMapper objectMapper,
            @Value("${character.storage-dir:./saves}") String storageDirectory) {
        this.objectMapper = objectMapper;
        this.storageDirectory = Path.of(storageDirectory);
    }

    @Async("characterSaveExecutor")
    public CompletableFuture<Void> saveAsync(CharacterSheet character) {
        try {
            Files.createDirectories(storageDirectory);

            String id = sanitize(character.getId() == null ? "current" : character.getId());
            Path target = storageDirectory.resolve(id + ".json");
            Path temp = storageDirectory.resolve(id + ".json.tmp");

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(temp.toFile(), character);

            try {
                Files.move(temp, target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar a ficha JSON", e);
        }
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
