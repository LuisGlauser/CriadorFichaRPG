package com.glauser.rpg.CriadorFichaRPG.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Usage {

    private String uses;

    private Map<String, Integer> usesByLevel;

    private List<String> recharge;
}