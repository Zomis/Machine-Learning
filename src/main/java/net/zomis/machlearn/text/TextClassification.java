package net.zomis.machlearn.text;

import net.zomis.machlearn.regression.LogisticRegression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class TextClassification {

    private final UnaryOperator<String> preprocess;
    private final TextFeatureMapper mapper;
    private final double[] theta;
    private final double threshold;

    public TextClassification(UnaryOperator<String> preprocess,
                              TextFeatureMapper mapper, double[] theta, double threshold) {
        this.preprocess = preprocess;
        this.mapper = mapper;
        this.theta = Arrays.copyOf(theta, theta.length);
        this.threshold = threshold;
        int featureCount = mapper.getFeatures().length;
        if (theta.length != featureCount + 1) {
            throw new IllegalArgumentException("Expects theta length (" + theta.length + ") to be " +
                    "equal to feature length (" + featureCount + ") + 1.");
        }
    }

    public double score(String text) {
        double[] features = mapper.toFeatures(preprocess.apply(text));
        return LogisticRegression.hypothesis(theta, features);
    }

    public Map<String, Double> getFeatures(String text) {
        String[] features = mapper.getFeatures();
        double[] mapFeatures = mapper.toFeatures(text);
        int featureCount = features.length;

        Map<String, Double> result = new HashMap<>();
        for (int i = 0; i < featureCount; i++) {
            if (mapFeatures[i] != 0) {
                // theta[0] is the bias value
                result.put(features[i], mapFeatures[i] * theta[i + 1]);
            }
        }
        return result;
    }

    public String preprocess(String text) {
        return preprocess.apply(text);
    }

    public boolean classify(String text) {
        return score(text) >= threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public TextFeatureMapper getMapper() {
        return this.mapper;
    }

}
