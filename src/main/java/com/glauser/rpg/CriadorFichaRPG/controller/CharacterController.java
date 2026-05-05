package com.glauser.rpg.CriadorFichaRPG.controller;
import com.glauser.rpg.CriadorFichaRPG.builder.CharacterBuilder;
import com.glauser.rpg.CriadorFichaRPG.dto.CharacterFormDTO;
import com.glauser.rpg.CriadorFichaRPG.registry.CharacterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/character")
public class CharacterController {

    @Autowired
    private CharacterRegistry registry;

    @PostMapping("/create")
    public Object create(@RequestBody CharacterFormDTO dto) {

        CharacterBuilder builder = new CharacterBuilder();

        builder.setName(dto.getName())
                .setClass(registry.getClass(dto.getClassName()))
                .setSpecies(registry.getSpecies(dto.getSpeciesName()))
                .setBackground(registry.getBackground(dto.getBackgroundName()));

        return builder.build();
    }

}