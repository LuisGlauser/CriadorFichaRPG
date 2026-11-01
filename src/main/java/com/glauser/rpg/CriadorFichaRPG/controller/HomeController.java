package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger log =
            LoggerFactory.getLogger(HomeController.class);

    private final CharacterService characterService;

    public HomeController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<CharacterSheet> characters =
                characterService.findAll();

        log.info("HOME: {} personagens recebidos do serviço.",
                characters.size());

        for (CharacterSheet character : characters) {
            log.info(
                    "HOME: personagem={} | id={} | classe={} | espécie={}",
                    character.getName(),
                    character.getId(),
                    character.getCharacterClass() != null
                            ? character.getCharacterClass().getName()
                            : "null",
                    character.getSpecies() != null
                            ? character.getSpecies().getName()
                            : "null"
            );
        }

        model.addAttribute("characters", characters);
        model.addAttribute("characterCount", characters.size());

        return "home";
    }
}