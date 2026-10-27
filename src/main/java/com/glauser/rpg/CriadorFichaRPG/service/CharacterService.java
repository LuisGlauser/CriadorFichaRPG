package com.glauser.rpg.CriadorFichaRPG.service;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.command.CharacterCommandInvoker;
import com.glauser.rpg.CriadorFichaRPG.command.HealCommand;
import com.glauser.rpg.CriadorFichaRPG.command.SetTemporaryHpCommand;
import com.glauser.rpg.CriadorFichaRPG.command.TakeDamageCommand;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
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

    private final CharacterCommandInvoker commandInvoker;

    public CharacterService(
            SpeciesRegistry speciesRegistry,
            BackgroundRegistry backgroundRegistry,
            ClassRegistry classRegistry,
            DerivedStatsObserver derivedStatsObserver,
            AutoSaveObserver autoSaveObserver,
            CharacterPersistenceService persistenceService,
            CharacterCommandInvoker commandInvoker) {

        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
        this.classRegistry = classRegistry;
        this.derivedStatsObserver = derivedStatsObserver;
        this.autoSaveObserver = autoSaveObserver;
        this.persistenceService = persistenceService;
        this.commandInvoker = commandInvoker;
    }

    public CharacterSheet create(CharacterCreationDTO dto) {

        Attributes attributes =
                new Attributes(
                        dto.getStrength(),
                        dto.getDexterity(),
                        dto.getConstitution(),
                        dto.getIntelligence(),
                        dto.getWisdom(),
                        dto.getCharisma()
                );

        CharacterSheet sheet =
                new CharacterBuilder()
                        .setName(dto.getName())
                        .setLevel(dto.getLevel())
                        .setSpecies(
                                speciesRegistry.getById(
                                        dto.getSpeciesId()
                                )
                        )
                        .setBackground(
                                backgroundRegistry.getById(
                                        dto.getBackgroundId()
                                )
                        )
                        .setClass(
                                classRegistry.getById(
                                        dto.getClassId()
                                )
                        )
                        .setAttributes(attributes)
                        .build();

        sheet.setId(UUID.randomUUID().toString());

        attachObservers(sheet);

        derivedStatsObserver.update(sheet);

        sheet.updateLifeState();

        persistenceService.save(sheet);

        return sheet;
    }

    public CharacterSheet getById(String id) {

        CharacterSheet sheet =
                persistenceService.findById(id);

        if (sheet != null) {
            attachObservers(sheet);
            sheet.updateLifeState();
        }

        return sheet;
    }

    public List<CharacterSheet> findAll() {
        return persistenceService.findAll();
    }

    public void save(CharacterSheet sheet) {
        persistenceService.save(sheet);
    }

    /**
     * Cura o personagem utilizando o State atual.
     *
     * Agora executado como um Command (HealCommand), o que
     * permite desfazer a cura depois via undoLast(id).
     */
    public CharacterSheet heal(String id) {

        CharacterSheet character = getById(id);

        if (character == null) {
            return null;
        }

        commandInvoker.execute(id, new HealCommand(), character);

        /*
         * O heal() já dispara o Observer,
         * mas salvamos explicitamente também para garantir
         * a persistência antes de retornar a resposta.
         */
        persistenceService.save(character);

        return character;
    }

    /**
     * Aplica dano ao personagem via TakeDamageCommand.
     *
     * A regra "primeiro tira do HP temporário, depois do HP atual"
     * está encapsulada em CharacterSheet#takeDamage, chamada de
     * dentro do comando.
     */
    public CharacterSheet damage(String id, int amount) {

        CharacterSheet character = getById(id);

        if (character == null) {
            return null;
        }

        commandInvoker.execute(id, new TakeDamageCommand(amount), character);

        persistenceService.save(character);

        return character;
    }

    /**
     * Define o HP temporário do personagem via SetTemporaryHpCommand.
     */
    public CharacterSheet setTemporaryHp(String id, int value) {

        CharacterSheet character = getById(id);

        if (character == null) {
            return null;
        }

        commandInvoker.execute(id, new SetTemporaryHpCommand(value), character);

        persistenceService.save(character);

        return character;
    }

    /**
     * Desfaz a última ação de combate (dano/cura/HP temporário)
     * executada para este personagem.
     */
    public CharacterSheet undoLast(String id) {

        CharacterSheet character = getById(id);

        if (character == null) {
            return null;
        }

        boolean undone = commandInvoker.undoLast(id, character);

        if (undone) {
            persistenceService.save(character);
        }

        return character;
    }

    private void attachObservers(CharacterSheet sheet) {

        sheet.addObserver(derivedStatsObserver);
        sheet.addObserver(autoSaveObserver);
    }

    /**
     * Compatibilidade com o fluxo antigo.
     */
    public CharacterSheet getLastCharacter() {

        List<CharacterSheet> all =
                persistenceService.findAll();

        if (all.isEmpty()) {
            return null;
        }

        CharacterSheet last =
                all.get(all.size() - 1);

        return getById(last.getId());
    }
}
