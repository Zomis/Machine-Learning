package net.zomis.machlearn.text;

import java.util.*;
import java.util.stream.Collectors;

public class TextFeatureWeights {

    private final double[] theta;
    private final String[] features;
    private final Map<String, Double> map;

    public TextFeatureWeights(String[] features, double[] theta) {
        if (theta.length != features.length + 1) {
            throw new IllegalArgumentException("Theta length must be same as feature length " +
                    "plus one for the bias weight");
        }
        this.theta = Arrays.copyOf(theta, theta.length);
        this.features = Arrays.copyOf(features, features.length);
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < features.length; i++) {
            map.put(features[i], theta[i + 1]);
        }
        this.map = map;
    }

    public Map<String, Double> getMap() {
        return map;
    }

    public List<Map.Entry<String, Double>> getMapByValue() {
        return map.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getValue))
            .collect(Collectors.toList());
    }

}
