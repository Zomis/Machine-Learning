package net.zomis.machlearn.common;

import net.zomis.machlearn.neural.LearningData;

import java.util.List;

/**
 * Represents a LearningDataSet that has been partitioned into training set, cross-validation set, and/or test-set.
 */
public class PartitionedDataSet {

    private final LearningDataSet trainingSet;
    private final LearningDataSet crossValidationSet;
    private final LearningDataSet testSet;

    public PartitionedDataSet(List<LearningData> trainingSet, List<LearningData> crossValidationSet,
                              List<LearningData> testSet) {
        this.trainingSet = new LearningDataSet(trainingSet);
        this.crossValidationSet = new LearningDataSet(crossValidationSet);
        this.testSet = new LearningDataSet(testSet);
    }

    public LearningDataSet getCrossValidationSet() {
        return crossValidationSet;
    }

    public LearningDataSet getTestSet() {
        return testSet;
    }

    public LearningDataSet getTrainingSet() {
        return trainingSet;
    }

}
