package com.glauser.rpg.CriadorFichaRPG.rest;

import com.glauser.rpg.CriadorFichaRPG.model.character.CharacterSheet;
import com.glauser.rpg.CriadorFichaRPG.model.character.Features;
import com.glauser.rpg.CriadorFichaRPG.model.content.Usage;

import java.util.List;
import java.util.OptionalInt;

/**
 * Regras sobre usos de features: quantos usos existem no nível atual
 * e em que tipo de descanso eles voltam.
 *
 * Só features cujo {@code usage.recharge} menciona descanso curto ou
 * longo são rastreadas; as demais (ex.: "once_per_turn") são ignoradas.
 */
public final class FeatureUses {

    private FeatureUses() {
    }

    /** "long_rest" / "longRest" / "LongRest" -> "longrest". */
    private static String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase().replace("_", "").replace(" ", "");
    }

    public static boolean rechargesOn(Features feature, RestType type) {

        Usage usage = feature.getUsage();

        if (usage == null || usage.getRecharge() == null) {
            return false;
        }

        List<String> recharge = usage.getRecharge();

        for (String entry : recharge) {

            String n = normalize(entry);

            // Descanso longo também devolve o que volta no curto.
            if (n.equals("shortrest")) {
                return true;
            }

            if (n.equals("longrest") && type == RestType.LONG) {
                return true;
            }
        }

        return false;
    }

    /** Está sujeita a descanso (curto ou longo)? */
    public static boolean isRestTracked(Features feature) {
        return rechargesOn(feature, RestType.LONG);
    }

    /**
     * Máximo de usos no nível atual, ou vazio se a feature não tem
     * contagem de usos que dependa de descanso.
     */
    public static OptionalInt maxUses(Features feature, CharacterSheet sheet) {

        if (!isRestTracked(feature)) {
            return OptionalInt.empty();
        }

        Usage usage = feature.getUsage();
        int level = Math.max(1, sheet.getLevel());

        if (usage.getUsesByLevel() != null && !usage.getUsesByLevel().isEmpty()) {

            for (var entry : usage.getUsesByLevel().entrySet()) {

                if (levelInRange(level, entry.getKey())) {
                    return OptionalInt.of(entry.getValue());
                }
            }

            return OptionalInt.empty();
        }

        String uses = usage.getUses();

        if (uses == null) {
            return OptionalInt.empty();
        }

        switch (normalize(uses)) {

            case "proficiencybonus":
                return OptionalInt.of(proficiencyBonus(level));

            case "charismamodifier":
                if (sheet.getAttributes() == null) {
                    return OptionalInt.empty();
                }
                int cha = sheet.getAttributes()
                        .getModifier(sheet.getAttributes().getCharisma());
                return OptionalInt.of(Math.max(1, cha));

            default:
                try {
                    return OptionalInt.of(Integer.parseInt(uses.trim()));
                } catch (NumberFormatException e) {
                    return OptionalInt.empty();
                }
        }
    }

    public static int proficiencyBonus(int level) {
        return 2 + (Math.max(1, level) - 1) / 4;
    }

    /** Aceita "5" e "3-5". */
    static boolean levelInRange(int level, String range) {

        if (range == null) {
            return false;
        }

        String[] parts = range.trim().split("-");

        try {
            int min = Integer.parseInt(parts[0].trim());
            int max = parts.length > 1
                    ? Integer.parseInt(parts[1].trim())
                    : min;

            return level >= min && level <= max;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
