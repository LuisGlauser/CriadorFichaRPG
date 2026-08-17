package com.glauser.rpg.CriadorFichaRPG.service;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import com.glauser.rpg.CriadorFichaRPG.observer.AutoSaveObserver;
import com.glauser.rpg.CriadorFichaRPG.observer.DerivedStatsObserver;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;
    private final ClassRegistry classRegistry;
    private final DerivedStatsObserver derivedStatsObserver;
    private final AutoSaveObserver autoSaveObserver;

    // armazenamento TEMPORÁRIO
    private CharacterSheet lastCharacter;

    public CharacterService(
            SpeciesRegistry speciesRegistry,
            BackgroundRegistry backgroundRegistry,
            ClassRegistry classRegistry,
            DerivedStatsObserver derivedStatsObserver,
            AutoSaveObserver autoSaveObserver) {

        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
        this.classRegistry = classRegistry;
        this.derivedStatsObserver = derivedStatsObserver;
        this.autoSaveObserver = autoSaveObserver;
    }

    public CharacterSheet create(CharacterCreationDTO dto) {

        CharacterBuilder builder = new CharacterBuilder();

        Attributes attributes = new Attributes(
                dto.getStrength(),
                dto.getDexterity(),
                dto.getConstitution(),
                dto.getIntelligence(),
                dto.getWisdom(),
                dto.getCharisma()
        );


        CharacterSheet sheet = builder
                .setName(dto.getName())
                .setLevel(dto.getLevel())
                .setSpecies(speciesRegistry.getById(dto.getSpeciesId()))
                .setBackground(backgroundRegistry.getById(dto.getBackgroundId()))
                .setClass(classRegistry.getById(dto.getClassId()))
                .setAttributes(attributes)
                .build();

        sheet.setId("current");

        sheet.addObserver(derivedStatsObserver);
        sheet.addObserver(autoSaveObserver);

        sheet.notifyObservers();

        this.lastCharacter = sheet;

        return sheet;
    }



    public CharacterSheet getLastCharacter() {
        return lastCharacter;
    }
}