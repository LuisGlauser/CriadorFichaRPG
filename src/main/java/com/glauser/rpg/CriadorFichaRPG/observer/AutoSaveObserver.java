package com.glauser.rpg.CriadorFichaRPG.observer;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterPersistenceService;
import org.springframework.stereotype.Component;

@Component
public class AutoSaveObserver implements CharacterObserver {

    private final CharacterPersistenceService persistenceService;

    public AutoSaveObserver(CharacterPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public void update(CharacterSheet character) {
        persistenceService.saveAsync(character);
    }
}
