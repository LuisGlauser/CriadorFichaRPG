package com.glauser.rpg.CriadorFichaRPG.service;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.persistence.SavedCharacter;
import com.glauser.rpg.CriadorFichaRPG.observer.AutoSaveObserver;
import com.glauser.rpg.CriadorFichaRPG.observer.DerivedStatsObserver;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CharacterService {
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;
    private final ClassRegistry classRegistry;
    private final DerivedStatsObserver derivedStatsObserver;
    private final AutoSaveObserver autoSaveObserver;
    private final CharacterPersistenceService persistenceService;

    public CharacterService(SpeciesRegistry speciesRegistry, BackgroundRegistry backgroundRegistry,
                            ClassRegistry classRegistry, DerivedStatsObserver derivedStatsObserver,
                            AutoSaveObserver autoSaveObserver, CharacterPersistenceService persistenceService) {
        this.speciesRegistry = speciesRegistry; this.backgroundRegistry = backgroundRegistry;
        this.classRegistry = classRegistry; this.derivedStatsObserver = derivedStatsObserver;
        this.autoSaveObserver = autoSaveObserver; this.persistenceService = persistenceService;
    }

    public CharacterSheet create(CharacterCreationDTO dto) {
        Attributes attributes = new Attributes(dto.getStrength(), dto.getDexterity(), dto.getConstitution(),
                dto.getIntelligence(), dto.getWisdom(), dto.getCharisma());

        CharacterSheet sheet = new CharacterBuilder()
                .setName(dto.getName()).setLevel(dto.getLevel())
                .setSpecies(speciesRegistry.getById(dto.getSpeciesId()))
                .setBackground(backgroundRegistry.getById(dto.getBackgroundId()))
                .setClass(classRegistry.getById(dto.getClassId()))
                .setAttributes(attributes).build();

        sheet.setId(UUID.randomUUID().toString());
        attachObservers(sheet);
        derivedStatsObserver.update(sheet);
        persistenceService.save(sheet);
        return sheet;
    }

    public CharacterSheet getById(String id) {
        CharacterSheet sheet = persistenceService.findById(id);
        if (sheet != null) attachObservers(sheet);
        return sheet;
    }

    public List<SavedCharacter> findAll() { return persistenceService.findAll(); }
    public void save(CharacterSheet sheet) { persistenceService.save(sheet); }

    private void attachObservers(CharacterSheet sheet) {
        sheet.addObserver(derivedStatsObserver);
        sheet.addObserver(autoSaveObserver);
    }

    // Compatibilidade com o fluxo antigo.
    public CharacterSheet getLastCharacter() {
        List<SavedCharacter> all = persistenceService.findAll();
        return all.isEmpty() ? null : getById(all.get(all.size() - 1).getId());
    }
}
