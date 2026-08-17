package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final CharacterService characterService;
    public HomeController(CharacterService characterService) { this.characterService = characterService; }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("characters", characterService.findAll());
        return "home";
    }
}
