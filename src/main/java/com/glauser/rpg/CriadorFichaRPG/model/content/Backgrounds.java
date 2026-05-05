package com.glauser.rpg.CriadorFichaRPG.model.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.prototype.Prototype;
import java.util.List;
import lombok.Data;

@Data
public class Backgrounds implements Prototype<Backgrounds> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private String description;

    private List<String> primaryAbilities;
    private String feat;

    private List<String> skillProficiencies;
    private List<String> toolProficiencies;

    private StartingEquipment startingEquipment;

    @Override
    public Backgrounds clone() {
        return mapper.convertValue(this, Backgrounds.class);
    }
}