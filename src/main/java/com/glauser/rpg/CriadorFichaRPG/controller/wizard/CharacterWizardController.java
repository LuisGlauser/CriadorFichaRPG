package com.glauser.rpg.CriadorFichaRPG.controller.wizard;

import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.controller.view.CharacterViewController;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/character/create")
@SessionAttributes("character")
public class CharacterWizardController {

    private final CharacterService characterService;
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;
    private final ClassRegistry classRegistry;

    public CharacterWizardController(CharacterService characterService, SpeciesRegistry speciesRegistry,
                                     BackgroundRegistry backgroundRegistry,
                                     ClassRegistry classRegistry) {
        this.characterService = characterService;
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
        this.classRegistry = classRegistry;
    }

    @ModelAttribute("character")
    public CharacterCreationDTO character() {
        return new CharacterCreationDTO();
    }

    // STEP 1
    @GetMapping("/step-1")
    public String step1() {
        return "character/wizard/step-1";
    }

    @PostMapping("/step-1")
    public String step1Post(@ModelAttribute("character") CharacterCreationDTO dto) {
        return "redirect:/character/create/step-2";
    }

    // STEP 2
    @GetMapping("/step-2")
    public String step2(Model model) {
        model.addAttribute("speciesList", speciesRegistry.findAll());
        model.addAttribute("backgroundList", backgroundRegistry.findAll());
        return "character/wizard/step-2";
    }

    @PostMapping("/step-2")
    public String step2Post(@ModelAttribute("character") CharacterCreationDTO dto) {

        System.out.println("Species: " + dto.getSpeciesId());
        System.out.println("Background: " + dto.getBackgroundId());

        return "redirect:/character/create/step-3";
    }

    // STEP 3
    @GetMapping("/step-3")
    public String step3(Model model) {
        model.addAttribute("classList", classRegistry.findAll());
        return "character/wizard/step-3";
    }

    @PostMapping("/step-3")
    public String step3Post(@ModelAttribute("character") CharacterCreationDTO dto) {
        return "redirect:/character/create/step-4";
    }

    @GetMapping("/step-4")
    public String step4(@ModelAttribute("character") CharacterCreationDTO dto,
                        Model model) {

        var characterClass = classRegistry.getById(dto.getClassId());
        if (characterClass == null) {
            return "redirect:/character/create/step-3";
        }
        Map<String, Features> features = new LinkedHashMap<>();

        for (var entry : characterClass.getFeatures().entrySet()) {

            Features f = entry.getValue();

            if ((f.getLevel() != null && f.getLevel() <= dto.getLevel()) ||
                    (f.getLevels() != null && f.getLevels().contains(dto.getLevel()))) {

                features.put(entry.getKey(), f);
            }
        }



        model.addAttribute("features", features);
        model.addAttribute("class", characterClass);


        return "character/wizard/step-4";
    }

    @PostMapping("/step-4")
    public String step4Post(@ModelAttribute("character") CharacterCreationDTO dto) {
        return "redirect:/character/create/step-6";
    }

    // STEP 6
    @GetMapping("/step-6")
    public String step6() {
        return "character/wizard/step-6";
    }

    @PostMapping("/step-6")
    public String step6Post(@ModelAttribute("character") CharacterCreationDTO dto) {
        return "redirect:/character/create/review";
    }

    @GetMapping("/review")
    public String review(@ModelAttribute("character") CharacterCreationDTO dto, Model model) {

        Attributes attributes = new Attributes(
                dto.getStrength(),
                dto.getDexterity(),
                dto.getConstitution(),
                dto.getIntelligence(),
                dto.getWisdom(),
                dto.getCharisma()
        );

        model.addAttribute("attr", attributes);

        model.addAttribute("character", dto);

        return "character/wizard/review";
    }

    @PostMapping("/finish")
    public String finish(@ModelAttribute("character") CharacterCreationDTO dto,
                         SessionStatus status) {

        characterService.create(dto);

        status.setComplete();

        return "redirect:/character/view";
    }
}