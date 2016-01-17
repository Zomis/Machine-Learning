package net.zomis.machlearn.neural

import groovy.transform.ToString

@ToString
class LearningData {

    double[] inputs
    double[] outputs

    LearningData(double[] inputs, double[] outputs) {
        this.inputs = inputs
        this.outputs = outputs
    }

}
