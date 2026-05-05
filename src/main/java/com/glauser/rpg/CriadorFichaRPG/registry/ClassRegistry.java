package com.glauser.rpg.CriadorFichaRPG.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.content.CharacterClass;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class ClassRegistry {

    private final Map<String, CharacterClass> classMap = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void load() {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources("classpath:data/classes/*.json");

            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {

                    CharacterClass clazz = mapper.readValue(is, CharacterClass.class);

                    String id = clazz.getName()
                            .toLowerCase()
                            .replace(" ", "_");

                    classMap.put(id, clazz);

                    System.out.println("Loaded: " + clazz.getName());

                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar classes", e);
        }
    }

    private void loadFile(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException("Arquivo não encontrado: " + path);
            }



            CharacterClass clazz = mapper.readValue(is, CharacterClass.class);

            String id = clazz.getName()
                    .toLowerCase()
                    .replace(" ", "_");

            classMap.put(id, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Erro carregando class: " + path, e);
        }
    }

    public List<CharacterClass> findAll() {
        return new ArrayList<>(classMap.values());
    }

    public CharacterClass getById(String id) {
        return classMap.get(id);
    }
}
