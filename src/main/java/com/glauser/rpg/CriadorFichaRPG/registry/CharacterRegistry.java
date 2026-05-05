package com.glauser.rpg.CriadorFichaRPG.registry;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import com.glauser.rpg.CriadorFichaRPG.model.content.Species;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class CharacterRegistry {

    private final Map<String, CharacterClass> classes = new HashMap<>();
    private final Map<String, Species> species = new HashMap<>();
    private final Map<String, Backgrounds> backgrounds = new HashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        loadAll("classpath:data/classes/*.json", CharacterClass.class, classes);
        loadAll("classpath:data/species/*.json", Species.class, species);
        loadAll("classpath:data/backgrounds/*.json", Backgrounds.class, backgrounds);

        System.out.println("Classes loaded: " + classes.keySet());
        System.out.println("Species loaded: " + species.keySet());
        System.out.println("Backgrounds loaded: " + backgrounds.keySet());
    }

    private <T> void loadAll(String pattern, Class<T> type, Map<String, T> map) {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources(pattern);

            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {

                    T obj = objectMapper.readValue(is, type);

                    String name = (String) type.getMethod("getName").invoke(obj);

                    map.put(name, obj);

                    System.out.println("Loaded: " + name);

                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar JSONs: " + pattern, e);
        }
    }

    // Prototype
    public CharacterClass getClass(String name) {
        return classes.get(name).clone();
    }

    public Species getSpecies(String name) {
        return species.get(name).clone();
    }

    public Backgrounds getBackground(String name) {
        return backgrounds.get(name).clone();
    }
}