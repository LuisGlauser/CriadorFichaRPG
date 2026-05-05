package com.glauser.rpg.CriadorFichaRPG.service;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;
    private final ClassRegistry classRegistry;

    // armazenamento TEMPORÁRIO
    private CharacterSheet lastCharacter;

    public CharacterService(SpeciesRegistry speciesRegistry,
                            BackgroundRegistry backgroundRegistry,
                            ClassRegistry classRegistry) {
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
        this.classRegistry = classRegistry;
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


        this.lastCharacter = sheet;

        return sheet;
    }



    public CharacterSheet getLastCharacter() {
        return lastCharacter;
    }
}