package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.mediator.CharacterMediator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CharacterViewController {

    private final CharacterService characterService;
    private final CharacterMediator characterMediator;

    public CharacterViewController(
            CharacterService characterService,
            CharacterMediator characterMediator) {
        this.characterService = characterService;
        this.characterMediator = characterMediator;
    }

    @GetMapping("/character/view")
    public String viewLastCharacter() {
        CharacterSheet character = characterService.getLastCharacter();
        return character == null
                ? "redirect:/"
                : "redirect:/character/view/" + character.getId();
    }

    @GetMapping("/character/view/{id}")
    public String viewCharacter(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        System.out.println("================================");
        System.out.println("ACESSANDO FICHA");
        System.out.println("ID RECEBIDO: " + id);
        System.out.println("================================");

        CharacterSheet baseCharacter = characterService.getById(id);
        if (baseCharacter == null) {
            System.out.println(
                    "ERRO: PERSONAGEM NÃO ENCONTRADO: " + id
            );

            return "redirect:/";
        }

        System.out.println(
                "PERSONAGEM ENCONTRADO: " + baseCharacter.getName()
        );

        String sessionKey = "effects-" + id;
        EffectsState effects = (EffectsState) session.getAttribute(sessionKey);
        if (effects == null) {
            effects = new EffectsState();
            session.setAttribute(sessionKey, effects);
        }

        CharacterComponent character = characterMediator.buildCharacter(baseCharacter, effects);

        model.addAttribute("character", character);
        model.addAttribute("effects", effects);
        model.addAttribute("activeEffects", character.getActiveEffects());
        return "character/view";
    }

    @PostMapping("/character/save/{id}")
    public String saveCharacter(@PathVariable String id) {
        CharacterSheet character = characterService.getById(id);
        if (character != null) {
            characterService.save(character);
        }
        return "redirect:/character/view/" + id;
    }
}
