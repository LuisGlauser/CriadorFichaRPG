package com.glauser.rpg.CriadorFichaRPG.model.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Data
@NoArgsConstructor
public class SavedCharacter {
    @Id private String id;
    @Column(nullable = false) private String name;
    private String className;
    private String speciesName;
    private int level;
    @Lob @Column(columnDefinition = "LONGTEXT", nullable = false) private String data;
    @Column(nullable = false) private LocalDateTime updatedAt;

    public SavedCharacter(String id, String name, String data) {
        this.id = id; this.name = name; this.data = data; this.updatedAt = LocalDateTime.now();
    }
}
