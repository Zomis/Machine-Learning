package net.zomis.machlearn.neural

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

    Stream<NeuronLink> links() {
        this.layers.stream()
            .skip(1)
            .flatMap({it.neurons.stream()})
            .flatMap({it.inputs.stream()})
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
            inputLayer.getNeurons().get(i).output = input[i]
        }

        int layerIndex = 0
        for (NeuronLayer layer : layers) {
            if (layerIndex++ == 0) {
                // Do not process input layer
                continue
            }
            for (Neuron node : layer) {
                node.process()
            }
        }
        for (int i = 0; i < outputLayer.size(); i++) {
            output[i] = outputLayer.getNeurons().get(i).output
        }
/*        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.getNeurons().get(i).output = 1
        }*/

        output
    }

    public NeuronLayer getLastLayer() {
        getLayer(layers.size() - 1)
    }

}
