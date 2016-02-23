package net.zomis.machlearn.text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TextFeatureBuilder {

    public static final Comparator<Map.Entry<String, Integer>> SORT_BY_VALUE =
        Comparator.<Map.Entry<String, Integer>, Integer>comparing(Map.Entry::getValue)
            .reversed();

    private final int nGrams;
    private final Map<String, Integer> counts;

    public TextFeatureBuilder(int nGrams) {
        if (nGrams < 1) {
            throw new IllegalArgumentException("nGrams must be positive, was " + nGrams);
        }
        this.nGrams = nGrams;
        this.counts = new HashMap<>();
    }

    public void add(String processed) {
        String[] sections = processed.split(" ");
        for (int i = 0; i <= sections.length - nGrams; i++) {
            String[] values = Arrays.copyOfRange(sections, i, i + nGrams);
            String value = String.join(" ", values);
            counts.merge(value, 1, Integer::sum);
        }

    }

    public TextFeatureMapper mapper(int maxLimit) {
        String[] features = counts.entrySet().stream()
            .sorted(SORT_BY_VALUE)
            .limit(maxLimit).map(Map.Entry::getKey)
            .collect(Collectors.toList())
            .toArray(new String[maxLimit]);
        return new TextFeatureMapper(features);
    }

    public Map<String, Integer> getCounts() {
        return new HashMap<>(counts);
    }
}
