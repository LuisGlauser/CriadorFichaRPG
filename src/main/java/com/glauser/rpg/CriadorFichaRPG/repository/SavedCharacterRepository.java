package com.glauser.rpg.CriadorFichaRPG.repository;

import com.glauser.rpg.CriadorFichaRPG.model.persistence.SavedCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedCharacterRepository extends JpaRepository<SavedCharacter, String> {
    List<SavedCharacter> findAllByOrderByNameAsc();
}
