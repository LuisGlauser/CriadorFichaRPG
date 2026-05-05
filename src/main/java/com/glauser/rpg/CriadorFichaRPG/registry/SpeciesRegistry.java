package com.glauser.rpg.CriadorFichaRPG.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SpeciesRegistry {

    private final Map<String, Species> speciesMap = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void load() {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources("classpath:data/species/*.json");

            for (Resource resource : resources) {

                Species species = mapper.readValue(resource.getInputStream(), Species.class);

                String id = species.getName()
                        .toLowerCase()
                        .replace(" ", "_");

                species.setId(id); // 🔥 essencial

                speciesMap.put(id, species);

                System.out.println("Loaded species: " + id);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro carregando species", e);
        }
    }

    public List<Species> findAll() {
        return new ArrayList<>(speciesMap.values());
    }

    public Species getById(String id) {
        return speciesMap.get(id);
    }
}