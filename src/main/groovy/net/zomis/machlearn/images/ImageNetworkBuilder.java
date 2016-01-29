package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.neural.NeuralNetwork;
import net.zomis.machlearn.neural.NeuronLayer;

import java.util.*;
import java.util.stream.Collectors;

public class ImageNetworkBuilder {

    private final NeuralNetwork network;
    private final Map<Object, List<double[]>> classifications = new HashMap<>();
    private final ImageAnalysis analysis;

    public ImageNetworkBuilder(ImageAnalysis analysis, int inputSize, int... hiddenLayerSizes) {
        this.analysis = analysis;
        this.network = new NeuralNetwork();
        NeuronLayer layer = this.network.createLayer("INPUT");
        for (int i = 0; i < inputSize; i++) {
            layer.createNeuron();
        }

        int hiddenIndex = 1;
        for (int layerSize : hiddenLayerSizes) {
            NeuronLayer parentLayer = layer;
            layer = this.network.createLayer("HIDDEN " + (hiddenIndex++));
            for (int i = 0; i < layerSize; i++) {
                layer.createNeuron().addInputs(parentLayer);
            }
        }
    }

    public ImageNetworkBuilder classify(Object result, double[] input) {
        classifications.putIfAbsent(result, new ArrayList<>());
        classifications.get(result).add(Arrays.copyOf(input, input.length));
        return this;
    }

    public ImageNetwork learn(Backpropagation backprop, Random random) {
        int outputNodes = classifications.size() - 1;

        NeuronLayer parentLayer = this.network.getLastLayer();
        NeuronLayer layer = this.network.createLayer("OUTPUT");
        for (int i = 0; i < outputNodes; i++) {
            layer.createNeuron().addInputs(parentLayer);
        }

        List<LearningData> learningData = new ArrayList<>();
        int outputIndex = 0;
        Object[] objects = new Object[outputNodes];
        for (Map.Entry<Object, List<double[]>> entry : classifications.entrySet()) {
            double[] outputs = new double[outputNodes];
            if (entry.getKey() != null) {
                outputs[outputIndex] = 1;
                objects[outputIndex] = entry.getKey();
                outputIndex++;
            }
            learningData.addAll(entry.getValue().stream()
                .map(inputs -> new LearningData(inputs, outputs))
                .collect(Collectors.toList()));
        }
        backprop.setLogRate(200);
        backprop.backPropagationLearning(learningData, network, random != null ? random : new Random());

        return new ImageNetwork(analysis, network, objects);
    }

    public ImageNetworkBuilder classifyNone(double[] input) {
        classifications.putIfAbsent(null, new ArrayList<>());
        classifications.get(null).add(Arrays.copyOf(input, input.length));
        return this;
    }

}
