package com.glauser.rpg.CriadorFichaRPG.rest;

import com.glauser.rpg.CriadorFichaRPG.command.CharacterCommandInvoker;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterSheetResponseDTO;
import com.glauser.rpg.CriadorFichaRPG.dto.RestStatusDTO;
import com.glauser.rpg.CriadorFichaRPG.dto.RestStatusDTO.FeatureUsesDTO;
import com.glauser.rpg.CriadorFichaRPG.memento.RestHistory;
import com.glauser.rpg.CriadorFichaRPG.memento.RestMemento;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Client do padrão Memento.
 *
 * Fluxo de um descanso:
 *   1. carrega a ficha;
 *   2. pede à ficha (Originator) um memento e entrega ao {@link RestHistory}
 *      (Caretaker);
 *   3. aplica as regras do descanso;
 *   4. salva.
 *
 * "Interromper descanso" faz o caminho inverso: tira o memento mais recente
 * do Caretaker e manda a ficha se restaurar a partir dele — como na regra
 * do jogo, em que um descanso interrompido não dá benefício algum.
 */
@Service
public class RestService {

    private final CharacterService characterService;
    private final RestHistory restHistory;
    private final CharacterCommandInvoker commandInvoker;

    public RestService(CharacterService characterService,
                       RestHistory restHistory,
                       CharacterCommandInvoker commandInvoker) {

        this.characterService = characterService;
        this.restHistory = restHistory;
        this.commandInvoker = commandInvoker;
    }

    // ------------------------------------------------------------------
    // Consulta
    // ------------------------------------------------------------------

    public RestStatusDTO status(String id) {

        CharacterSheet sheet = characterService.getById(id);

        return sheet == null ? null : toStatus(sheet, null);
    }

    // ------------------------------------------------------------------
    // Descansos
    // ------------------------------------------------------------------

    /**
     * Descanso curto: gasta dados de vida para recuperar HP e devolve
     * as features que recarregam em descanso curto.
     *
     * Cada dado gasto cura o valor médio do dado + modificador de CON
     * (sem rolagem aleatória, para o resultado ser reproduzível).
     */
    public RestStatusDTO shortRest(String id, int hitDiceToSpend) {

        CharacterSheet sheet = characterService.getById(id);

        if (sheet == null) {
            return null;
        }

        takeSnapshot(id, sheet, RestType.SHORT);

        int spend = Math.max(0, Math.min(hitDiceToSpend, hitDiceRemaining(sheet)));
        int healed = healFromHitDice(sheet, spend);
        int restored = restoreFeatures(sheet, RestType.SHORT);

        String message = RestType.SHORT.getLabel() + ": gastou " + spend
                + " dado(s) de vida e recuperou " + healed + " HP"
                + featuresSuffix(restored) + ".";

        return finish(id, sheet, message);
    }

    /**
     * Descanso longo: HP no máximo, HP temporário zerado, metade dos
     * dados de vida (mínimo 1) de volta e todas as features recarregadas.
     */
    public RestStatusDTO longRest(String id) {

        CharacterSheet sheet = characterService.getById(id);

        if (sheet == null) {
            return null;
        }

        takeSnapshot(id, sheet, RestType.LONG);

        int hpBefore = sheet.getCurrentHp();
        int recoveredDice = Math.min(
                sheet.getHitDiceSpent(),
                Math.max(1, sheet.getLevel() / 2)
        );

        sheet.updateBatch(() -> {
            sheet.setCurrentHp(sheet.getMaxHp());
            sheet.setTemporaryHp(0);
            sheet.setHitDiceSpent(sheet.getHitDiceSpent() - recoveredDice);
        });

        int restored = restoreFeatures(sheet, RestType.LONG);

        String message = RestType.LONG.getLabel() + ": recuperou "
                + (sheet.getMaxHp() - hpBefore) + " HP, "
                + recoveredDice + " dado(s) de vida"
                + featuresSuffix(restored) + ".";

        return finish(id, sheet, message);
    }

    /**
     * Interrompe o último descanso: devolve a ficha ao estado em que
     * estava antes dele.
     *
     * @return o novo status, ou {@code null} se a ficha não existe
     * @throws IllegalStateException se não há descanso para interromper
     */
    public RestStatusDTO interruptRest(String id) {

        CharacterSheet sheet = characterService.getById(id);

        if (sheet == null) {
            return null;
        }

        RestMemento memento = restHistory.pop(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Não há descanso para interromper."));

        sheet.restore(memento);
        characterService.save(sheet);

        return toStatus(sheet,
                "Descanso interrompido: ficha restaurada ao estado \""
                        + memento.label() + "\".");
    }

    // ------------------------------------------------------------------
    // Uso de features (é o que gasta os recursos que o descanso restaura)
    // ------------------------------------------------------------------

    public RestStatusDTO useFeature(String id, String featureName) {

        CharacterSheet sheet = characterService.getById(id);

        if (sheet == null) {
            return null;
        }

        Features feature = findFeature(sheet, featureName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Feature não encontrada: " + featureName));

        OptionalInt max = FeatureUses.maxUses(feature, sheet);

        if (max.isEmpty()) {
            throw new IllegalArgumentException(
                    "A feature \"" + featureName + "\" não tem usos limitados por descanso.");
        }

        int spent = sheet.getFeatureUsesSpent()
                .getOrDefault(feature.getName(), 0);

        if (spent >= max.getAsInt()) {
            throw new IllegalStateException(
                    "Sem usos restantes de \"" + featureName + "\".");
        }

        Map<String, Integer> updated = new HashMap<>(sheet.getFeatureUsesSpent());
        updated.put(feature.getName(), spent + 1);
        sheet.setFeatureUsesSpent(updated);

        characterService.save(sheet);

        return toStatus(sheet, "Usou " + feature.getName() + ".");
    }

    // ------------------------------------------------------------------
    // Internos
    // ------------------------------------------------------------------

    private void takeSnapshot(String id, CharacterSheet sheet, RestType type) {

        restHistory.push(id, sheet.createMemento("Antes do "
                + type.getLabel().toLowerCase()));

        /*
         * O descanso encerra o "combate": desfazer um dano antigo depois
         * dele restauraria um HP que já não faz sentido.
         */
        commandInvoker.clear(id);
    }

    private RestStatusDTO finish(String id, CharacterSheet sheet, String message) {

        characterService.save(sheet);

        return toStatus(sheet, message);
    }

    private int hitDiceRemaining(CharacterSheet sheet) {
        return Math.max(0, sheet.getLevel() - sheet.getHitDiceSpent());
    }

    private int healFromHitDice(CharacterSheet sheet, int dice) {

        if (dice <= 0 || sheet.getCharacterClass() == null) {
            return 0;
        }

        int hitDie = sheet.getCharacterClass().getHitDie();
        int conMod = sheet.getAttributes() == null
                ? 0
                : sheet.getAttributes().getConModifier();

        int perDie = Math.max(0, hitDie / 2 + 1 + conMod);
        int before = sheet.getCurrentHp();
        int after = Math.min(sheet.getMaxHp(), before + perDie * dice);

        sheet.updateBatch(() -> {
            sheet.setCurrentHp(after);
            sheet.setHitDiceSpent(sheet.getHitDiceSpent() + dice);
        });

        return after - before;
    }

    /** Zera os usos gastos das features que recarregam neste descanso. */
    private int restoreFeatures(CharacterSheet sheet, RestType type) {

        Map<String, Integer> updated = new HashMap<>(sheet.getFeatureUsesSpent());
        int restored = 0;

        if (sheet.getFeatures() != null) {
            for (Features f : sheet.getFeatures()) {

                if (FeatureUses.rechargesOn(f, type)
                        && updated.getOrDefault(f.getName(), 0) > 0) {

                    updated.remove(f.getName());
                    restored++;
                }
            }
        }

        if (restored > 0) {
            sheet.setFeatureUsesSpent(updated);
        }

        return restored;
    }

    private String featuresSuffix(int restored) {
        return restored > 0
                ? " e recarregou " + restored + " feature(s)"
                : "";
    }

    private Optional<Features> findFeature(CharacterSheet sheet, String name) {

        if (sheet.getFeatures() == null || name == null) {
            return Optional.empty();
        }

        return sheet.getFeatures().stream()
                .filter(f -> name.equals(f.getName()))
                .findFirst();
    }

    private RestStatusDTO toStatus(CharacterSheet sheet, String message) {

        List<FeatureUsesDTO> features = new ArrayList<>();

        if (sheet.getFeatures() != null) {
            for (Features f : sheet.getFeatures()) {

                OptionalInt max = FeatureUses.maxUses(f, sheet);

                if (max.isPresent()) {

                    int spent = Math.min(
                            sheet.getFeatureUsesSpent().getOrDefault(f.getName(), 0),
                            max.getAsInt()
                    );

                    features.add(new FeatureUsesDTO(
                            f.getName(),
                            spent,
                            max.getAsInt(),
                            max.getAsInt() - spent,
                            FeatureUses.rechargesOn(f, RestType.SHORT)
                                    ? "Descanso curto"
                                    : "Descanso longo"
                    ));
                }
            }
        }

        Optional<RestMemento> last = restHistory.peek(sheet.getId());

        int hitDie = sheet.getCharacterClass() == null
                ? 0
                : sheet.getCharacterClass().getHitDie();

        return new RestStatusDTO(
                CharacterSheetResponseDTO.from(sheet),
                hitDie,
                hitDiceRemaining(sheet),
                sheet.getLevel(),
                last.isPresent(),
                last.map(RestMemento::label).orElse(null),
                message,
                features
        );
    }
}
