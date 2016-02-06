package net.zomis.gameai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureExtractors<T> {

    private final List<FeatureExtractor<T>> extractors = new ArrayList<>();
    private final Class<T> clazz;

    public FeatureExtractors(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<Feature<?>> extract(T object) {
        return extractors.stream()
            .map(ex -> ex.extract(object))
            .collect(Collectors.toList());
    }

}
