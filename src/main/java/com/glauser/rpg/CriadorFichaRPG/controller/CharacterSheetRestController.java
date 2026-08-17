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

    public CharacterSheetRestController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CharacterSheetResponseDTO> patch(
            @PathVariable String id,
            @RequestBody CharacterPatchDTO patch) {

        CharacterSheet character = getCharacter(id);
        if (character == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"current".equals(character.getId())) {
            return ResponseEntity.notFound().build();
        }

        applyPatch(character, patch);
        return ResponseEntity.ok(CharacterSheetResponseDTO.from(character));
    }

    /**
     * PUT é aceito para clientes que preferem atualização via PUT.
     * Campos nulos são ignorados, mantendo o comportamento seguro de PATCH.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CharacterSheetResponseDTO> put(
            @PathVariable String id,
            @RequestBody CharacterPatchDTO update) {
        return patch(id, update);
    }

    private CharacterSheet getCharacter(String id) {
        CharacterSheet character = characterService.getLastCharacter();
        if (character == null || !id.equals(character.getId())) {
            return null;
        }
        return character;
    }

    private void applyPatch(CharacterSheet c, CharacterPatchDTO p) {
        c.updateBatch(() -> {
            if (p.name() != null) c.setName(p.name());
            if (p.level() != null) c.setLevel(Math.max(1, p.level()));
            if (p.currentHp() != null) c.setCurrentHp(Math.max(0, p.currentHp()));
            if (p.temporaryHp() != null) c.setTemporaryHp(p.temporaryHp());
            if (p.notes() != null) c.setNotes(p.notes());

            if (p.armorType() != null) c.setArmorType(p.armorType());
            if (p.armorBase() != null) c.setArmorBase(Math.max(0, p.armorBase()));
            if (p.shieldBonus() != null) c.setShieldBonus(Math.max(0, p.shieldBonus()));

            if (p.strength() != null) c.updateAttribute("strength", p.strength());
            if (p.dexterity() != null) c.updateAttribute("dexterity", p.dexterity());
            if (p.constitution() != null) c.updateAttribute("constitution", p.constitution());
            if (p.intelligence() != null) c.updateAttribute("intelligence", p.intelligence());
            if (p.wisdom() != null) c.updateAttribute("wisdom", p.wisdom());
            if (p.charisma() != null) c.updateAttribute("charisma", p.charisma());
        });
    }
}
