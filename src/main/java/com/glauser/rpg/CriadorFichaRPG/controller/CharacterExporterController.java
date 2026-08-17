package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.adapter.CharacterExporter;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.service.CharacterService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/character/export")
public class CharacterExporterController {
    private final CharacterService characterService;
    private final List<CharacterExporter> exporters;

    public CharacterExporterController(CharacterService characterService, List<CharacterExporter> exporters) {
        this.characterService = characterService; this.exporters = exporters;
    }

    @GetMapping("/{id}/{format}")
    public ResponseEntity<byte[]> export(@PathVariable String id, @PathVariable String format) {
        CharacterSheet sheet = characterService.getById(id);
        if (sheet == null) return ResponseEntity.notFound().build();

        CharacterExporter exporter = exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format)).findFirst().orElse(null);
        if (exporter == null) return ResponseEntity.badRequest()
                .body(("Formato inválido: " + format).getBytes(StandardCharsets.UTF_8));

        String content = exporter.export(sheet);
        String fileName = sheet.getName().replaceAll("[^a-zA-Z0-9À-ÿ._-]", "_") + "." + format.toLowerCase();
        MediaType type = format.equalsIgnoreCase("json") ? MediaType.APPLICATION_JSON : MediaType.TEXT_PLAIN;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(type)
                .body(content.getBytes(StandardCharsets.UTF_8));
    }
}
