package net.zomis.neuralone

import java.util.stream.Stream

class NeuralNetwork {

    List<NeuronLayer> layers = []

    NeuronLayer getInputLayer() {
        getLayer(0)
    }

    NeuronLayer getOutputLayer() {
        getLayer(layers.size() - 1)
    }

    NeuronLayer getLayer(int layerIndex) {
        return layers.get(layerIndex)
    }

    NeuronLayer createLayer(String name) {
        def layer = new NeuronLayer(name)
        this.layers << layer
        return layer
    }

    int getLayerCount() {
        layers.size()
    }

    Stream<NeuronConnection> links() {
        this.layers.stream().flatMap({it.neurons.stream()}).flatMap({it.outputs.stream()})
    }

    void printAll() {
        println "$layerCount layers:"
        layers.stream().forEach({
            it.printNodes()
            println()
        })
        println()
    }

    double[] run(double[] input) {
        double[] output = new double[outputLayer.size()]
        assert input.length == inputLayer.size()
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.getNeurons().get(i).weight0 = input[i]
        }

        for (NeuronLayer layer : layers) {
            for (Neuron node : layer) {
                node.calculateInput()
                node.calculateOutput(node.input)
            }
        }
        for (int i = 0; i < outputLayer.size(); i++) {
            output[i] = outputLayer.getNeurons().get(i).output
        }
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.getNeurons().get(i).weight0 = 1
        }

        output
    }
}
