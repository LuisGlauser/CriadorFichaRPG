package com.glauser.rpg.CriadorFichaRPG.controller;

import com.glauser.rpg.CriadorFichaRPG.adapter.CharacterExporter;
import com.glauser.rpg.CriadorFichaRPG.facade.CharacterSheetFacade;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de exportação.
 *
 * Usa o Facade para buscar o personagem e os Adapters para exportar.
 * O Spring injeta automaticamente todos os beans que implementam
 * CharacterExporter (Json, Csv e Txt).
 *
 * Endpoints:
 *   GET /character/export/json  → baixa ficha.json
 *   GET /character/export/csv   → baixa ficha.csv
 *   GET /character/export/txt   → baixa ficha.txt
 */
@RestController
@RequestMapping("/character/export")
public class CharacterExporterController {

    private final CharacterSheetFacade facade;
    private final List<CharacterExporter> exporters;

    public CharacterExporterController(CharacterSheetFacade facade,
                                     List<CharacterExporter> exporters) {
        this.facade = facade;
        this.exporters = exporters;
    }

    @GetMapping("/{format}")
    public ResponseEntity<String> export(@PathVariable String format) {

        CharacterSheet sheet = facade.getLastCharacter();

        if (sheet == null) {
            return ResponseEntity.notFound().build();
        }

        CharacterExporter exporter = exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElse(null);

        if (exporter == null) {
            return ResponseEntity.badRequest()
                    .body("Formato inválido: '" + format + "'. Use: json, csv ou txt.");
        }

        String conteudo = exporter.export(sheet);
        String nomeArquivo = sheet.getName().replaceAll("\\s+", "_") + "." + format.toLowerCase();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(conteudo);
    }
}
