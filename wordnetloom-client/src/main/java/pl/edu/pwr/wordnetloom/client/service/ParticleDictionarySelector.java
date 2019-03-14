package pl.edu.pwr.wordnetloom.client.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.ParticleElementType;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ParticleDictionarySelector {

    private ObservableList<Dictionary> particles = FXCollections.observableArrayList();

    private Map<ParticleElementType, List<Dictionary>> particlesMap = new HashMap<>();

    @PostConstruct
    public void init() {
        particlesMap.put(ParticleElementType.prefix, Dictionaries.getDictionary(Dictionaries.PREFIXES_DICTIONARY));
        particlesMap.put(ParticleElementType.interfix, Dictionaries.getDictionary(Dictionaries.INTERFIXES_DICTIONARY));
        particlesMap.put(ParticleElementType.suffix, Dictionaries.getDictionary(Dictionaries.SUFFIXES_DICTIONARY));
    }

    public void setParticleElementType(ParticleElementType type){
        particles.clear();
        if (type == null) {
            return;
        }
        if (particlesMap.containsKey(type)) {
            particles.addAll(particlesMap.get(type));
        }
    }

    public ObservableList<Dictionary> availableParticles() {
        return particles;
    }

}
