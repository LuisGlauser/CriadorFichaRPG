package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterPatchDTO;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterSheetResponseDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/characters")
public class CharacterSheetRestController {

    private final CharacterService characterService;

    public CharacterSheetRestController(
            CharacterService characterService) {

        this.characterService = characterService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CharacterSheetResponseDTO> patch(
            @PathVariable String id,
            @RequestBody CharacterPatchDTO patch) {

        CharacterSheet character =
                characterService.getById(id);

        if (character == null) {
            return ResponseEntity.notFound().build();
        }

        applyPatch(character, patch);

        characterService.save(character);

        return ResponseEntity.ok(
                CharacterSheetResponseDTO.from(character)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterSheetResponseDTO> put(
            @PathVariable String id,
            @RequestBody CharacterPatchDTO update) {

        return patch(id, update);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> save(
            @PathVariable String id) {

        CharacterSheet character =
                characterService.getById(id);

        if (character == null) {
            return ResponseEntity.notFound().build();
        }

        characterService.save(character);

        return ResponseEntity.ok().build();
    }

    /**
     * Prova de conceito do State.
     *
     * O controller não decide:
     * - Normal cura 15
     * - Sangrando cura 20
     * - Inconsciente cura 30
     *
     * Quem decide isso é o State.
     */
    @PostMapping("/{id}/heal")
    public ResponseEntity<CharacterSheetResponseDTO> heal(
            @PathVariable String id) {

        CharacterSheet character =
                characterService.heal(id);

        if (character == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                CharacterSheetResponseDTO.from(character)
        );
    }

    private void applyPatch(
            CharacterSheet c,
            CharacterPatchDTO p) {

        c.updateBatch(() -> {

            if (p.name() != null) {
                c.setName(p.name());
            }

            if (p.level() != null) {
                c.setLevel(
                        Math.max(1, p.level())
                );
            }

            if (p.currentHp() != null) {
                c.setCurrentHp(
                        Math.max(0, p.currentHp())
                );
            }

            if (p.temporaryHp() != null) {
                c.setTemporaryHp(
                        p.temporaryHp()
                );
            }

            if (p.notes() != null) {
                c.setNotes(p.notes());
            }

            if (p.armorType() != null) {
                c.setArmorType(p.armorType());
            }

            if (p.armorBase() != null) {
                c.setArmorBase(
                        Math.max(0, p.armorBase())
                );
            }

            if (p.shieldBonus() != null) {
                c.setShieldBonus(
                        Math.max(0, p.shieldBonus())
                );
            }

            if (p.strength() != null) {
                c.updateAttribute(
                        "strength",
                        p.strength()
                );
            }

            if (p.dexterity() != null) {
                c.updateAttribute(
                        "dexterity",
                        p.dexterity()
                );
            }

            if (p.constitution() != null) {
                c.updateAttribute(
                        "constitution",
                        p.constitution()
                );
            }

            if (p.intelligence() != null) {
                c.updateAttribute(
                        "intelligence",
                        p.intelligence()
                );
            }

            if (p.wisdom() != null) {
                c.updateAttribute(
                        "wisdom",
                        p.wisdom()
                );
            }

            if (p.charisma() != null) {
                c.updateAttribute(
                        "charisma",
                        p.charisma()
                );
            }
        });
    }
}
