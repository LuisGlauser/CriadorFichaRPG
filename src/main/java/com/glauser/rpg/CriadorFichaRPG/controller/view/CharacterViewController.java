package com.glauser.rpg.CriadorFichaRPG.controller.view;

import com.glauser.rpg.CriadorFichaRPG.decorator.EffectsState;
import com.glauser.rpg.CriadorFichaRPG.decorator.ItemBoostDecorator;
import com.glauser.rpg.CriadorFichaRPG.decorator.TemporaryHpDecorator;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterComponent;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CharacterViewController {

    private final CharacterService characterService;

    public CharacterViewController(CharacterService characterService) {
        this.characterService = characterService;
    }

    // ── GET /character/view ──────────────────────────────────────────────────
    @GetMapping("/character/view")
    public String viewCharacter(HttpSession session, Model model) {

        CharacterSheet sheet = characterService.getLastCharacter();
        if (sheet == null) {
            model.addAttribute("character", null);
            return "character/view";
        }

        // Recupera (ou cria) o estado de efeitos da sessão
        EffectsState state = getOrCreateState(session);

        // Monta a cadeia de decorators conforme o estado ativo
        CharacterComponent character = sheet;

        if (state.isGlovesEquipped()) {
            character = new ItemBoostDecorator(character,
                    "Gauntlets of Ogre Power", "strength", 19);
        }
        if (state.getTemporaryHp() > 0) {
            character = new TemporaryHpDecorator(character, state.getTemporaryHp());
        }

        model.addAttribute("character", character);
        model.addAttribute("effects",   state);          // para os checkboxes/inputs
        model.addAttribute("activeEffects", character.getActiveEffects());
        return "character/view";
    }

    // ── POST /character/effects — aplica/remove efeitos ─────────────────────
    @PostMapping("/character/effects")
    public String applyEffects(
            @RequestParam(required = false) String action,
            @RequestParam(required = false, defaultValue = "0") int tempHp,
            HttpSession session) {

        EffectsState state = getOrCreateState(session);

        switch (action != null ? action : "") {
            case "equip_gloves"   -> state.setGlovesEquipped(true);
            case "unequip_gloves" -> state.setGlovesEquipped(false);
            case "set_temp_hp"    -> state.setTemporaryHp(tempHp);
            case "clear_temp_hp"  -> state.setTemporaryHp(0);
            case "clear_all"      -> state.removeAllEffects();
        }

        session.setAttribute("effectsState", state);
        return "redirect:/character/view";
    }

    // ── helper ───────────────────────────────────────────────────────────────
    private EffectsState getOrCreateState(HttpSession session) {
        EffectsState state = (EffectsState) session.getAttribute("effectsState");
        if (state == null) {
            state = new EffectsState();
            session.setAttribute("effectsState", state);
        }
        return state;
    }
}
