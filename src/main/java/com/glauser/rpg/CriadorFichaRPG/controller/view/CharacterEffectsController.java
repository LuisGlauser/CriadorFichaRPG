package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CharacterEffectsController {

    @PostMapping("/character/effects")
    public String handleEffect(
            @RequestParam("action") String action,
            @RequestParam(value = "tempHp", required = false) Integer tempHp,
            HttpSession session) {

        EffectsState effects =
                (EffectsState) session.getAttribute("effects");

        if (effects == null) {
            effects = new EffectsState();
            session.setAttribute("effects", effects);
        }

        switch (action) {

            case "equip_gloves" -> {
                effects.setGlovesEquipped(true);
            }

            case "unequip_gloves" -> {
                effects.setGlovesEquipped(false);
            }

            case "set_temp_hp" -> {
                effects.setTemporaryHp(tempHp != null ? tempHp : 0);
            }

            case "clear_temp_hp" -> {
                effects.setTemporaryHp(0);
            }

            case "clear_all" -> {
                effects.removeAllEffects();
            }
        }

        return "redirect:/character/view";
    }
}