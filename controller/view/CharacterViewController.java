package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.ItemBoostDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.TemporaryHpDecorator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CharacterViewController {

    private final CharacterService characterService;

    public CharacterViewController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/character/view")
    public String viewCharacter(
            @RequestParam(required = false, defaultValue = "false") boolean item,
            @RequestParam(required = false, defaultValue = "0")     int tempHp,
            Model model) {

        CharacterSheet sheet = characterService.getLastCharacter();

        if (sheet == null) {
            model.addAttribute("character", null);
            return "character/view";
        }

        // Começa com o objeto base e empilha decorators conforme necessário
        CharacterComponent character = sheet;

        if (item) {
            character = new ItemBoostDecorator(character,
                    "Gauntlets of Ogre Power", "strength", 19);
        }

        if (tempHp > 0) {
            character = new TemporaryHpDecorator(character, tempHp);
        }

        model.addAttribute("character", character);
        model.addAttribute("activeEffects", character.getActiveEffects());
        return "character/view";
    }
}
