package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.CharacterSheetDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.decorator.ItemBoostDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.TemporaryHpDecorator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CharacterViewController {

    private final CharacterService characterService;

    public CharacterViewController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/character/view")
    public String viewCharacter(
            Model model,
            HttpSession session) {

        CharacterSheet baseCharacter =
                characterService.getLastCharacter();

        if (baseCharacter == null) {
            return "redirect:/character/create";
        }

        EffectsState effects =
                (EffectsState) session.getAttribute("effects");

        if (effects == null) {
            effects = new EffectsState();
            session.setAttribute("effects", effects);
        }

        CharacterComponent character = baseCharacter;

        if (effects.isGlovesEquipped()) {
            character = new ItemBoostDecorator(
                    character,
                    "Gauntlets of Ogre Power",
                    "strength",
                    19
            );
        }

        if (effects.getTemporaryHp() > 0) {
            character = new TemporaryHpDecorator(
                    character,
                    effects.getTemporaryHp()
            );
        }

        model.addAttribute("character", character);
        model.addAttribute("effects", effects);
        model.addAttribute("activeEffects",
                character.getActiveEffects());

        return "character/view";
    }
}