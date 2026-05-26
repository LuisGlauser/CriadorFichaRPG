package com.glauser.rpg.CriadorFichaRPG.adapter;

import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * Adapter — adapta StringJoiner para a interface CharacterExporter.
 *
 * StringJoiner é uma classe Java padrão que monta strings com separadores.
 * Ela não sabe nada sobre CharacterSheet. Este Adapter traduz a ficha
 * para o formato CSV usando o StringJoiner como mecanismo interno.
 */
@Component
public class CSVExporterAdapter implements CharacterExporter {

    @Override
    public String export(CharacterSheet sheet) {
        // StringJoiner: classe "incompatível" sendo adaptada
        StringJoiner csv = new StringJoiner("\n");

        // Cabeçalho
        csv.add("campo,valor");

        // Dados básicos
        csv.add("nome,"    + escapeCsv(sheet.getName()));
        csv.add("nivel,"   + sheet.getLevel());
        csv.add("classe,"  + escapeCsv(sheet.getCharacterClass().getName()));
        csv.add("raca,"    + escapeCsv(sheet.getSpecies() != null ? sheet.getSpecies().getName() : ""));
        csv.add("background," + escapeCsv(sheet.getBackground() != null ? sheet.getBackground().getName() : ""));
        csv.add("hp_maximo,"  + sheet.getMaxHp());
        csv.add("hp_atual,"   + sheet.getCurrentHp());

        // Atributos
        Attributes a = sheet.getAttributes();
        csv.add("forca,"        + a.getStrength());
        csv.add("destreza,"     + a.getDexterity());
        csv.add("constituicao," + a.getConstitution());
        csv.add("inteligencia," + a.getIntelligence());
        csv.add("sabedoria,"    + a.getWisdom());
        csv.add("carisma,"      + a.getCharisma());

        // Features
        if (sheet.getFeatures() != null) {
            for (Features f : sheet.getFeatures()) {
                String nivel = f.getLevel() != null ? String.valueOf(f.getLevel()) : "1";
                csv.add("feature_nv" + nivel + "," + escapeCsv(f.getName()));
            }
        }

        return csv.toString();
    }

    @Override
    public String getFormat() {
        return "csv";
    }

    // Escapa vírgulas e aspas para CSV válido
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
