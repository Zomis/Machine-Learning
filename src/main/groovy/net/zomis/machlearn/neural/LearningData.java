package net.zomis.machlearn.neural;

import java.util.Arrays;

public class LearningData {

    public final double[] inputs;
    public final double[] outputs;
    public final double weight;

    public LearningData(double[] inputs, double[] outputs) {
        this(inputs, outputs, 1);
    }

    public LearningData(double[] inputs, double[] outputs, double weight) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.weight = weight;
    }

    public double getInput(int i) {
        return inputs[i];
    }

    @Override
    public String toString() {
        return "LearningData{" +
                "inputs=" + Arrays.toString(inputs) +
                ", outputs=" + Arrays.toString(outputs) +
                '}';
    }

    public double[] getOutputs() {
        return outputs;
    }

    public double[] getInputs() {
        return inputs;
    }
}
