package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;

/**
 * Garante que classe, espécie e background escolhidos existem nos registries.
 * Evita, por exemplo, NullPointerException no CharacterBuilder.calculateHp().
 */
public class RegistryExistsValidator extends ValidationHandler {

    private final ClassRegistry classRegistry;
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;

    public RegistryExistsValidator(ClassRegistry classRegistry,
                                   SpeciesRegistry speciesRegistry,
                                   BackgroundRegistry backgroundRegistry) {
        this.classRegistry = classRegistry;
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
    }

    @Override
    protected void check(CharacterCreationDTO dto) {
        String classId = dto.getClassId();
        String speciesId = dto.getSpeciesId();
        String backgroundId = dto.getBackgroundId();

        if (classId == null || classId.isBlank() || classRegistry.getById(classId) == null) {
            throw new ValidationException("Classe inválida ou não selecionada.");
        }

        if (speciesId == null || speciesId.isBlank() || speciesRegistry.getById(speciesId) == null) {
            throw new ValidationException("Espécie inválida ou não selecionada.");
        }

        if (backgroundId == null || backgroundId.isBlank() || backgroundRegistry.getById(backgroundId) == null) {
            throw new ValidationException("Background inválido ou não selecionado.");
        }
    }
}
