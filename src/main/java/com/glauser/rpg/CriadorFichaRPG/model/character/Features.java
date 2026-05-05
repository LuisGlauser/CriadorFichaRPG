package com.glauser.rpg.CriadorFichaRPG.model.character;

import com.glauser.rpg.CriadorFichaRPG.model.content.Effect;
import com.glauser.rpg.CriadorFichaRPG.model.content.Usage;
import com.glauser.rpg.CriadorFichaRPG.prototype.Prototype;
import lombok.Data;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Features implements Prototype<Features> {

    private String name;

    private Integer level;
    private List<Integer> levels;

    private Map<Integer, Integer> levelScaling; // ✔️ corrigido

    private List<Effect> effects;

    private String activation;
    private String trigger;

    private Usage usage;
    private String resourceInteraction;

    @Override
    public Features clone() {
        Features copy = new Features();

        copy.name = this.name;
        copy.level = this.level;
        copy.levels = this.levels != null ? List.copyOf(this.levels) : null;
        copy.levelScaling = this.levelScaling != null ? Map.copyOf(this.levelScaling) : null;
        copy.effects = this.effects != null ? List.copyOf(this.effects) : null;

        copy.activation = this.activation;
        copy.trigger = this.trigger;
        copy.usage = this.usage;
        copy.resourceInteraction = this.resourceInteraction;

        return copy;
    }
}