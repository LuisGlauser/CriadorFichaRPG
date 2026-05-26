package com.glauser.rpg.CriadorFichaRPG.adapter;

import com.glauser.rpg.CriadorFichaRPG.model.character.Attributes;
import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import org.springframework.stereotype.Component;

@Component
public class TxtExporterAdapter implements CharacterExporter {

    @Override
    public String export(CharacterSheet sheet) {
        // StringBuilder: classe "incompatível" sendo adaptada
        StringBuilder sb = new StringBuilder();

        linha(sb, "═══════════════════════════════════════");
        sb.append("  ").append(sheet.getName().toUpperCase()).append("\n");
        linha(sb, "═══════════════════════════════════════");

        sb.append("Classe    : ").append(sheet.getCharacterClass().getName())
                .append("  |  Nível: ").append(sheet.getLevel()).append("\n");

        if (sheet.getSpecies() != null) {
            sb.append("Raça      : ").append(sheet.getSpecies().getName()).append("\n");
        }
        if (sheet.getBackground() != null) {
            sb.append("Background: ").append(sheet.getBackground().getName()).append("\n");
        }

        sb.append("HP        : ").append(sheet.getCurrentHp())
                .append(" / ").append(sheet.getMaxHp()).append("\n");

        linha(sb, "───────────────────────────────────────");
        sb.append("ATRIBUTOS\n");
        linha(sb, "───────────────────────────────────────");

        Attributes a = sheet.getAttributes();
        sb.append(atributo("FOR", a.getStrength(),   a))
                .append(atributo("DES", a.getDexterity(),  a))
                .append(atributo("CON", a.getConstitution(), a))
                .append(atributo("INT", a.getIntelligence(), a))
                .append(atributo("SAB", a.getWisdom(),     a))
                .append(atributo("CAR", a.getCharisma(),   a));

        if (sheet.getFeatures() != null && !sheet.getFeatures().isEmpty()) {
            linha(sb, "───────────────────────────────────────");
            sb.append("FEATURES DA CLASSE\n");
            linha(sb, "───────────────────────────────────────");

            for (Features f : sheet.getFeatures()) {
                String nivel = f.getLevel() != null ? "Nv." + f.getLevel() : "Nv.1";
                sb.append("  [").append(nivel).append("] ").append(f.getName()).append("\n");
            }
        }

        linha(sb, "═══════════════════════════════════════");

        return sb.toString();
    }

    @Override
    public String getFormat() {
        return "txt";
    }

    private void linha(StringBuilder sb, String separador) {
        sb.append(separador).append("\n");
    }

    private String atributo(String nome, int valor, Attributes a) {
        int mod = a.getModifier(valor);
        String modStr = mod >= 0 ? "+" + mod : String.valueOf(mod);
        return String.format("  %-4s %2d  (%s)%n", nome, valor, modStr);
    }
}
