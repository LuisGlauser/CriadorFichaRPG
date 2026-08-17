package com.glauser.rpg.CriadorFichaRPG.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.persistence.SavedCharacter;
import com.glauser.rpg.CriadorFichaRPG.repository.SavedCharacterRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CharacterPersistenceService {
    private final ObjectMapper objectMapper;
    private final SavedCharacterRepository repository;

    public CharacterPersistenceService(ObjectMapper objectMapper, SavedCharacterRepository repository) {
        this.objectMapper = objectMapper; this.repository = repository;
    }

    @Async("characterSaveExecutor")
    public void saveAsync(CharacterSheet character) { save(character); }

    public CharacterSheet save(CharacterSheet character) {
        try {
            if (character.getId() == null || character.getId().isBlank())
                throw new IllegalArgumentException("A ficha precisa ter um ID antes de ser salva.");
            String json = objectMapper.writeValueAsString(character);
            SavedCharacter saved = repository.findById(character.getId()).orElseGet(SavedCharacter::new);
            saved.setId(character.getId());
            saved.setName(character.getName());
            saved.setClassName(character.getCharacterClass() != null ? character.getCharacterClass().getName() : "");
            saved.setSpeciesName(character.getSpecies() != null ? character.getSpecies().getName() : "");
            saved.setLevel(character.getLevel());
            saved.setData(json);
            saved.setUpdatedAt(LocalDateTime.now()); repository.save(saved);
            return character;
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível salvar a ficha no banco de dados.", e);
        }
    }

    public CharacterSheet findById(String id) {
        return repository.findById(id).map(saved -> {
            try { return objectMapper.readValue(saved.getData(), CharacterSheet.class); }
            catch (Exception e) { throw new IllegalStateException("Não foi possível ler a ficha " + id, e); }
        }).orElse(null);
    }

    public List<SavedCharacter> findAll() { return repository.findAllByOrderByNameAsc(); }
}
