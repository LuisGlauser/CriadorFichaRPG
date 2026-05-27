package com.glauser.rpg.CriadorFichaRPG.facade;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


@Component
public class CharacterSheetFacade {

    private final CharacterService characterService;
    private final ClassRegistry classRegistry;
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;

    public CharacterSheetFacade(CharacterService characterService,
                                ClassRegistry classRegistry,
                                SpeciesRegistry speciesRegistry,
                                BackgroundRegistry backgroundRegistry) {
        this.characterService = characterService;
        this.classRegistry = classRegistry;
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
    }

    public CharacterSheet buildAndSave(CharacterCreationDTO dto) {
        return characterService.create(dto);
    }

    public Map<String, Features> getFeaturesUpToLevel(String classId, int level) {
        CharacterClass characterClass = classRegistry.getById(classId);

        if (characterClass == null) {
            return Map.of();
        }

        Map<String, Features> filtered = new LinkedHashMap<>();

        for (var entry : characterClass.getFeatures().entrySet()) {
            Features f = entry.getValue();

            boolean porLevel  = f.getLevel() != null && f.getLevel() <= level;
            boolean porLevels = f.getLevels() != null && f.getLevels().stream().anyMatch(l -> l <= level);

            if (porLevel || porLevels) {
                filtered.put(entry.getKey(), f);
            }
        }

        return filtered;
    }

    public CharacterSheet getLastCharacter() {
        return characterService.getLastCharacter();
    }

    public CharacterClass getClass(String classId) {
        return classRegistry.getById(classId);
    }
}