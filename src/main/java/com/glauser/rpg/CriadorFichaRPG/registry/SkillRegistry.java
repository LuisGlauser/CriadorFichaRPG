package com.glauser.rpg.CriadorFichaRPG.registry;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SkillRegistry {

    private final List<String> skills = List.of(
            "Acrobatics",
            "Animal Handling",
            "Arcana",
            "Athletics",
            "Deception",
            "History",
            "Insight",
            "Intimidation",
            "Investigation",
            "Medicine",
            "Nature",
            "Perception",
            "Performance",
            "Persuasion",
            "Religion",
            "Sleight of Hand",
            "Stealth",
            "Survival"
    );

    public List<String> getAll() {
        return skills;
    }
}