package net.zomis.machlearn.common;

import net.zomis.machlearn.neural.LearningData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LearningDataSet {

    private final List<LearningData> data;

    public LearningDataSet() {
        this(new ArrayList<>());
    }

    public LearningDataSet(List<LearningData> data) {
        this.data = new ArrayList<>(data);
    }

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

    public Stream<LearningData> stream() {
        return data.stream();
    }

    public PartitionedDataSet partition(double trainingSetRatio,
        double crossValidationSetRatio, double testSetRatio, Random random) {
        List<LearningData> shuffledData = new ArrayList<>(this.data);
        Collections.shuffle(shuffledData, random);
        // Calculate the sum to support ratios like 0.1, 0.2, 0.3
        double sum = trainingSetRatio + crossValidationSetRatio + testSetRatio;
        int size = shuffledData.size();
        int indexSplit1 = (int) (trainingSetRatio / sum * size);
        int indexSplit2 = indexSplit1 + (int) (crossValidationSetRatio / sum * size);
        List<LearningData> trainingSet =
            new ArrayList<>(shuffledData.subList(0, indexSplit1));
        List<LearningData> crossValidationSet =
                new ArrayList<>(shuffledData.subList(indexSplit1, indexSplit2));
        List<LearningData> testSet =
                new ArrayList<>(shuffledData.subList(indexSplit2, size));
        return new PartitionedDataSet(trainingSet, crossValidationSet, testSet);
    }

}
