package net.zomis.machlearn.common;

import net.zomis.machlearn.neural.LearningData;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LearningDataSet that has been partitioned into training set, cross-validation set, and/or test-set.
 */
public class PartitionedDataSet {

    private final List<LearningData> trainingSet;
    private final List<LearningData> crossValidationSet;
    private final List<LearningData> testSet;

    public PartitionedDataSet(List<LearningData> trainingSet, List<LearningData> crossValidationSet,
                              List<LearningData> testSet) {
        this.trainingSet = new ArrayList<>(trainingSet);
        this.crossValidationSet = new ArrayList<>(crossValidationSet);
        this.testSet = new ArrayList<>(testSet);
    }

    public List<LearningData> getCrossValidationSet() {
        return new ArrayList<>(crossValidationSet);
    }

    public List<LearningData> getTestSet() {
        return new ArrayList<>(testSet);
    }

    public List<LearningData> getTrainingSet() {
        return new ArrayList<>(trainingSet);
    }

}
