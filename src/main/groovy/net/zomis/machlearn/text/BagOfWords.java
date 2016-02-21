package net.zomis.machlearn.text;

import java.util.HashMap;
import java.util.Map;

public class BagOfWords {
    private final Map<String, Integer> counts = new HashMap<>();

    public String[] addText(String s) {
        String cleaned = s.toLowerCase().replaceAll("['\"\\.\\(\\),]", " ");
        String[] split = cleaned.split(" ");
        for (String key : split) {
            if (!key.isEmpty()) {
                counts.merge(key, 1, (a, b) -> a + b);
            }
        }
        return split;
    }

    Map<String, Integer> getData() {
        return new HashMap<>(counts);
    }

}
