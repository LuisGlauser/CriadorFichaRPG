package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.mediator.CharacterMediator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CharacterEffectsController {

    private final CharacterMediator characterMediator;

    public CharacterEffectsController(CharacterMediator characterMediator) {
        this.characterMediator = characterMediator;
    }

    @PostMapping("/character/effects/{id}")
    public String handleEffect(
            @PathVariable String id,
            @RequestParam("action") String action,
            @RequestParam(value = "tempHp", required = false) Integer tempHp,
            HttpSession session) {

        String sessionKey = "effects-" + id;
        EffectsState effects = (EffectsState) session.getAttribute(sessionKey);

        if (effects == null) {
            effects = new EffectsState();
            session.setAttribute(sessionKey, effects);
        }

        characterMediator.handleEffect(effects, action, tempHp);
        return "redirect:/character/view/" + id;
    }
}
