package com.glauser.rpg.CriadorFichaRPG.chain.validation;

import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import org.springframework.stereotype.Component;

/**
 * Monta a cadeia de validação da criação de personagem.
 *
 * Ordem: nome -> nível -> classe/espécie/background -> atributos.
 */
@Component
public class ValidationChainFactory {

    private final ClassRegistry classRegistry;
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;

    public ValidationChainFactory(ClassRegistry classRegistry,
                                  SpeciesRegistry speciesRegistry,
                                  BackgroundRegistry backgroundRegistry) {
        this.classRegistry = classRegistry;
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
    }

    public ValidationHandler create() {
        ValidationHandler first = new NameValidator();

        first.setNext(new LevelValidator())
             .setNext(new RegistryExistsValidator(
                     classRegistry, speciesRegistry, backgroundRegistry))
             .setNext(new AttributesValidator());

        return first;
    }
}
