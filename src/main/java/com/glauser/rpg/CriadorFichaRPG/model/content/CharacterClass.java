package com.glauser.rpg.CriadorFichaRPG.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.prototype.Prototype;
import java.util.List;
import java.util.Map;

import lombok.Data;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CharacterClass implements Prototype<CharacterClass> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private int hitDie;

    private List<String> savingThrowProficiencies;
    private SkillProficiency skillProficiencies;

    private List<String> weaponProficiencies;
    private List<String> armorTraining;

    private StartingEquipment startingEquipment;

    private Map<String, Features> features;

    @Override
    public CharacterClass clone() {
        return mapper.convertValue(this, CharacterClass.class);
    }
}