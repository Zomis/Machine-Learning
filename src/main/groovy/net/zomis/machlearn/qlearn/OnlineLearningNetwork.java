package net.zomis.machlearn.qlearn;

import net.zomis.machlearn.neural.LearningData;

public interface OnlineLearningNetwork {

    double[] run(double[] input);
    void learn(LearningData data);

}
