package com.glauser.rpg.CriadorFichaRPG.dto;

import java.util.List;

/**
 * Resposta dos endpoints de descanso: a ficha atualizada mais o estado
 * dos recursos que o descanso restaura.
 */
public record RestStatusDTO(
        CharacterSheetResponseDTO sheet,
        int hitDie,
        int hitDiceRemaining,
        int hitDiceTotal,
        boolean canInterrupt,
        String lastRestLabel,
        String message,
        List<FeatureUsesDTO> features
) {

    public record FeatureUsesDTO(
            String name,
            int spent,
            int max,
            int remaining,
            String recharge
    ) {
    }
}
