package com.glauser.rpg.CriadorFichaRPG.model.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.prototype.Prototype;
import java.util.List;
import lombok.Data;

@Data
public class Species implements Prototype<Species> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private String creatureType;
    private Size size;
    private Speed speed;
    private List<Features> traits;

    @Override
    public Species clone() {
        return mapper.convertValue(this, Species.class);
    }
}