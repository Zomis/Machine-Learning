package net.zomis.machlearn.neural;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralMain {

    public static void main(String[] args) {
        NeuralNetwork network = new NeuralNetwork();

        NeuronLayer inputLayer = network.createLayer("IN");
        inputLayer.createNeuron();
        inputLayer.createNeuron();

        NeuronLayer middleLayer = network.createLayer("MIDDLE");
        middleLayer.createNeuron().addInputs(inputLayer);
        middleLayer.createNeuron().addInputs(inputLayer);

        NeuronLayer outputLayer = network.createLayer("OUT");
        outputLayer.createNeuron().addInputs(middleLayer);
        outputLayer.createNeuron().addInputs(middleLayer);

        List<LearningData> examples = new ArrayList<>();
        examples.add(new LearningData(new double[]{0, 0}, new double[]{0, 0}));
        examples.add(new LearningData(new double[]{0, 1}, new double[]{0, 1}));
        examples.add(new LearningData(new double[]{1, 0}, new double[]{0, 1}));
        examples.add(new LearningData(new double[]{1, 1}, new double[]{1, 1}));
        new Backpropagation(0.2, 100000).backPropagationLearning(examples, network);

        network.printAll();

        for (LearningData data : examples) {
            double[] output = network.run(data.inputs);
            System.out.println(Arrays.toString(data.getInputs()) + " --> " + Arrays.toString(output));
            network.printAll();
            System.out.println("-----------------");
        }
    }

}
