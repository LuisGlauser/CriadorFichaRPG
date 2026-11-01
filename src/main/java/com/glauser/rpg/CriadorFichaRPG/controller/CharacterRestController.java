package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.dto.RestStatusDTO;
import com.glauser.rpg.CriadorFichaRPG.rest.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * Endpoints de descanso (Memento) e de uso de features.
 */
@RestController
@RequestMapping("/api/characters/{id}")
public class CharacterRestController {

    private final RestService restService;

    public CharacterRestController(RestService restService) {
        this.restService = restService;
    }

    @GetMapping("/rest")
    public ResponseEntity<RestStatusDTO> status(@PathVariable String id) {
        return respond(() -> restService.status(id));
    }

    @PostMapping("/rest/short")
    public ResponseEntity<RestStatusDTO> shortRest(
            @PathVariable String id,
            @RequestParam(name = "hitDice", defaultValue = "0") int hitDice) {

        return respond(() -> restService.shortRest(id, hitDice));
    }

    @PostMapping("/rest/long")
    public ResponseEntity<RestStatusDTO> longRest(@PathVariable String id) {
        return respond(() -> restService.longRest(id));
    }

    /** Restaura a ficha ao estado anterior ao último descanso. */
    @PostMapping("/rest/interrupt")
    public ResponseEntity<RestStatusDTO> interrupt(@PathVariable String id) {
        return respond(() -> restService.interruptRest(id));
    }

    @PostMapping("/features/use")
    public ResponseEntity<RestStatusDTO> useFeature(
            @PathVariable String id,
            @RequestParam("name") String name) {

        return respond(() -> restService.useFeature(id, name));
    }

    private ResponseEntity<RestStatusDTO> respond(Supplier<RestStatusDTO> action) {

        try {
            RestStatusDTO result = action.get();

            return result == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(result);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
