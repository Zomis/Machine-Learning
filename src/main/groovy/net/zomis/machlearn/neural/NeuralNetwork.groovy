package net.zomis.machlearn.neural

import java.nio.file.Path
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

    void save(OutputStream output) {
        new DataOutputStream(output).withCloseable {
            it.writeInt(layerCount)
            for (NeuronLayer layer : layers) {
                it.writeInt(layer.size())
                it.writeInt(layer.name.length())
                it.writeBytes(layer.name)
            }
            for (NeuronLayer layer : layers) {
                for (Neuron neuron : layer) {
                    for (NeuronLink link : neuron.inputs) {
                        it.writeDouble(link.weight)
                    }
                }
            }
        }
    }

    static NeuralNetwork load(InputStream input) {
        def network = new NeuralNetwork()
        new DataInputStream(input).withCloseable {
            int layers = it.readInt()
            for (int i = 0; i < layers; i++) {
                int size = it.readInt()
                int nameLength = it.readInt()
                StringBuilder name = new StringBuilder()
                for (int nameIndex = 0; nameIndex < nameLength; nameIndex++) {
                    name.append((char) it.readByte())
                }
                NeuronLayer layer = network.createLayer(name.toString())
                size.times { layer.createNeuron() }
            }
            for (int i = 0; i < layers; i++) {
                NeuronLayer layer = network.getLayer(i)
                if (i > 0) {
                    layer.neurons.each {it.addInputs(network.getLayer(i - 1))}
                }
                for (Neuron neuron : layer) {
                    for (NeuronLink link : neuron.inputs) {
                        link.weight = it.readDouble()
                    }
                }
            }
        }
        network
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
