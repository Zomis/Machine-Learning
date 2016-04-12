package net.zomis.machlearn.text;

import net.zomis.machlearn.regression.LogisticRegression;

import java.util.Arrays;
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
    }

    public double score(String text) {
        double[] features = mapper.toFeatures(preprocess.apply(text));
        return LogisticRegression.hypothesis(theta, features);
    }

    public boolean classify(String text) {
        return score(text) >= threshold;
    }

    public double getThreshold() {
        return threshold;
    }

}
