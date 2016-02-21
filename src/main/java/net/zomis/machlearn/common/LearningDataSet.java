package net.zomis.machlearn.common;

import net.zomis.machlearn.neural.LearningData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LearningDataSet {

    private final List<LearningData> data = new ArrayList<>();

    public void add(Object representation, double[] x, double y) {
        this.add(representation, x, new double[]{y});
    }

    public void add(Object representation, double[] x, double[] y) {
        this.data.add(new LearningData(representation, x, y));
    }

    public double[][] getXs() {
        return data.stream()
            .map(LearningData::getInputs)
            .collect(Collectors.toList()).toArray(new double[data.size()][]);
    }

    public double[] getY() {
        return data.stream()
            .map(LearningData::getOutputs)
            .mapToDouble(d -> d[0]).toArray();
    }

    public int numFeaturesWithZero() {
        return data.get(0).getInputs().length + 1;
    }

    public PrecisionRecallF1 precisionRecallF1(double[] theta, ClassifierFunction hypothesis) {
        PrecisionRecallF1 score = new PrecisionRecallF1();
        for (LearningData ld : data) {
            boolean prediction = hypothesis.classify(theta, ld.getInputs());
            boolean actual = ld.getOutputs()[0] >= 0.5;
            score.add(actual, prediction);
        }
        return score;
    }

    public List<LearningData> getData() {
        return data;
    }
}
