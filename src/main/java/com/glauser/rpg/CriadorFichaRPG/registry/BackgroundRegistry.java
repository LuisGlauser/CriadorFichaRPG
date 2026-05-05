package com.glauser.rpg.CriadorFichaRPG.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glauser.rpg.CriadorFichaRPG.model.content.Backgrounds;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.*;

@Component
public class BackgroundRegistry {

    private final Map<String, Backgrounds> map = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void load() {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources("classpath:data/backgrounds/*.json");

            for (Resource resource : resources) {

                Backgrounds bg = mapper.readValue(resource.getInputStream(), Backgrounds.class);

                String id = bg.getName()
                        .toLowerCase()
                        .replace(" ", "_");

                bg.setId(id); // 🔥 essencial

                map.put(id, bg);

                System.out.println("Loaded background: " + id);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro carregando backgrounds", e);
        }
    }

    private void loadFile(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {

            Backgrounds bg = mapper.readValue(is, Backgrounds.class);

            String id = bg.getName()
                    .toLowerCase()
                    .replace(" ", "_");

            bg.setId(id); // 🔥 ESSA LINHA TAMBÉM

            map.put(id, bg);

        } catch (Exception e) {
            throw new RuntimeException("Erro carregando background: " + path, e);
        }
    }

    public List<Backgrounds> findAll() {
        return new ArrayList<>(map.values());
    }

    public Backgrounds getById(String id) {
        return map.get(id);
    }
}