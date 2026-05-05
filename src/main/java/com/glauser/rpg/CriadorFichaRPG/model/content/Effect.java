package com.glauser.rpg.CriadorFichaRPG.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Effect {

    private String type;
    private String resource;

    private String formula;
    private String category;
    private Integer amount;
    private String trigger;
    private String sense;
    private int resultHP;
    private String activation;

    private String action;
    private String condition;
    private int range;
    private String unit;

    private String distance;
    private Boolean noOpportunityAttacks;

    private String bonus;
    private String duration;
    private String choice;
}
