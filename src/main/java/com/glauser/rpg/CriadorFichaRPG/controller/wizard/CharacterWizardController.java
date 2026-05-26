package com.glauser.rpg.CriadorFichaRPG.controller.wizard;

import com.glauser.rpg.CriadorFichaRPG.dto.CharacterCreationDTO;
import com.glauser.rpg.CriadorFichaRPG.facade.CharacterSheetFacade;
import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.registry.BackgroundRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.ClassRegistry;
import com.glauser.rpg.CriadorFichaRPG.registry.SpeciesRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/character/create")
@SessionAttributes("character")
public class CharacterWizardController {

    /*
     * Antes: 4 dependências diretas (CharacterService, ClassRegistry,
     *        SpeciesRegistry, BackgroundRegistry) + lógica de negócio inline.
     *
     * Depois: o Facade encapsula a complexidade de criação e consulta.
     * ClassRegistry, SpeciesRegistry e BackgroundRegistry ainda são
     * injetados apenas para popular listas dos selects — o que é
     * responsabilidade do controller (UI), não do Facade (negócio).
     */
    private final CharacterSheetFacade facade;
    private final ClassRegistry classRegistry;
    private final SpeciesRegistry speciesRegistry;
    private final BackgroundRegistry backgroundRegistry;

    public CharacterWizardController(CharacterSheetFacade facade,
                                     ClassRegistry classRegistry,
                                     SpeciesRegistry speciesRegistry,
                                     BackgroundRegistry backgroundRegistry) {
        this.facade = facade;
        this.classRegistry = classRegistry;
        this.speciesRegistry = speciesRegistry;
        this.backgroundRegistry = backgroundRegistry;
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

    // STEP 4 — lógica de filtro delegada ao Facade
    @GetMapping("/step-4")
    public String step4(@ModelAttribute("character") CharacterCreationDTO dto, Model model) {
        var characterClass = classRegistry.getById(dto.getClassId());
        if (characterClass == null) {
            return "redirect:/character/create/step-3";
        }

        // Antes: ~12 linhas de loop e if aqui mesmo no controller
        // Agora: uma chamada ao Facade
        model.addAttribute("features", facade.getFeaturesUpToLevel(dto.getClassId(), dto.getLevel()));
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

    // REVIEW
    @GetMapping("/review")
    public String review(@ModelAttribute("character") CharacterCreationDTO dto, Model model) {
        Attributes attributes = new Attributes(
                dto.getStrength(), dto.getDexterity(), dto.getConstitution(),
                dto.getIntelligence(), dto.getWisdom(), dto.getCharisma()
        );
        model.addAttribute("attr", attributes);
        model.addAttribute("character", dto);
        return "character/wizard/review";
    }


    @PostMapping("/finish")
    public String finish(@ModelAttribute("character") CharacterCreationDTO dto,
                         SessionStatus status) {
        facade.buildAndSave(dto);
        status.setComplete();
        return "redirect:/character/view";
    }
}