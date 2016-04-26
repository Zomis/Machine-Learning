package net.zomis.machlearn.text;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class TextFeatureMapper {

    private final String[] features;

    public TextFeatureMapper(String... features) {
        if (features.length == 0) {
            throw new IllegalArgumentException(
                "Cannot create a feature mapper without any features");
        }
        this.features = Arrays.copyOf(features, features.length);
    }
    
    public double[] toFeatures(String str) {
        return IntStream.range(0, features.length)
            .mapToObj(i -> features[i])
            .map(s -> Pattern.compile("\\b" + Pattern.quote(s) + "\\b"))
            .mapToDouble(p -> p.matcher(str).find() ? 1 : 0)
            .toArray();
    }

    public String[] getFeatures() {
        return Arrays.copyOf(features, features.length);
    }

}
