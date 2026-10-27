package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.decorator.ItemBoostDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.TemporaryHpDecorator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CharacterViewController {
    private final CharacterService characterService;
    public CharacterViewController(CharacterService characterService) { this.characterService = characterService; }

    @GetMapping("/character/view")
    public String viewLastCharacter() {
        CharacterSheet character = characterService.getLastCharacter();
        return character == null ? "redirect:/" : "redirect:/character/view/" + character.getId();
    }

    @GetMapping("/character/view/{id}")
    public String viewCharacter(@PathVariable String id, Model model, HttpSession session) {
        CharacterSheet baseCharacter = characterService.getById(id);
        if (baseCharacter == null) return "redirect:/";

        EffectsState effects = (EffectsState) session.getAttribute("effects-" + id);
        if (effects == null) { effects = new EffectsState(); session.setAttribute("effects-" + id, effects); }

        CharacterComponent character = baseCharacter;
        if (effects.isGlovesEquipped()) character = new ItemBoostDecorator(character, "Gauntlets of Ogre Power", "strength", 19);
        if (effects.getTemporaryHp() > 0) character = new TemporaryHpDecorator(character, effects.getTemporaryHp());

        model.addAttribute("character", character);
        model.addAttribute("effects", effects);
        model.addAttribute("activeEffects", character.getActiveEffects());
        return "character/view";
    }

    @PostMapping("/character/save/{id}")
    public String saveCharacter(@PathVariable String id) {
        CharacterSheet character = characterService.getById(id);
        if (character != null) characterService.save(character);
        return "redirect:/character/view/" + id;
    }
}
