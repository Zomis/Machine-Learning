package net.zomis.machlearn.neural;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NeuralNetwork {

    public List<NeuronLayer> layers = new ArrayList<>();

    public List<NeuronLayer> getLayers() {
        return layers;
    }

    public NeuronLayer getInputLayer() {
        return getLayer(0);
    }

    public NeuronLayer getOutputLayer() {
        return getLayer(layers.size() - 1);
    }

    public NeuronLayer getLayer(int layerIndex) {
        return layers.get(layerIndex);
    }

    public NeuronLayer createLayer(String name) {
        NeuronLayer layer = new NeuronLayer(name);
        this.layers.add(layer);
        return layer;
    }

    public int getLayerCount() {
        return layers.size();
    }

    public Stream<NeuronLink> links() {
        return this.layers.stream()
            .skip(1)
            .flatMap(it -> it.neurons.stream())
            .flatMap(it -> it.inputs.stream());
    }

    public void printAll() {
        System.out.println(getLayerCount() + " layers:");
        layers.stream().forEach(it -> {
            it.printNodes();
            System.out.println();
        });
        System.out.println();
    }

    void save(OutputStream output) {
        try (DataOutputStream it = new DataOutputStream(output)) {
            it.writeInt(this.getLayerCount());
            for (NeuronLayer layer : layers) {
                it.writeInt(layer.size());
                it.writeInt(layer.name.length());
                it.writeBytes(layer.name);
            }
            for (NeuronLayer layer : layers) {
                for (Neuron neuron : layer) {
                    for (NeuronLink link : neuron.inputs) {
                        it.writeDouble(link.getWeight());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static NeuralNetwork load(InputStream input) {
        NeuralNetwork network = new NeuralNetwork();
        try (DataInputStream it = new DataInputStream(input)) {
            int layers = it.readInt();
            for (int i = 0; i < layers; i++) {
                int size = it.readInt();
                int nameLength = it.readInt();
                StringBuilder name = new StringBuilder();
                for (int nameIndex = 0; nameIndex < nameLength; nameIndex++) {
                    name.append((char) it.readByte());
                }
                NeuronLayer layer = network.createLayer(name.toString());
                for (int j = 0; j < size; j++) {
                    layer.createNeuron();
                }
            }
            for (int i = 0; i < layers; i++) {
                NeuronLayer layer = network.getLayer(i);
                if (i > 0) {
                    final int ii = i;
                    layer.neurons.forEach(it2 -> it2.addInputs(network.getLayer(ii - 1)));
                }
                for (Neuron neuron : layer) {
                    for (NeuronLink link : neuron.inputs) {
                        link.setWeight(it.readDouble());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return network;
    }

    public double[] run(double[] input) {
        double[] output = new double[getOutputLayer().size()];
        assert input.length == getInputLayer().size();
        for (int i = 0; i < getInputLayer().size(); i++) {
            getInputLayer().neurons.get(i).output = input[i];
        }

        int layerIndex = 0;
        for (NeuronLayer layer : layers) {
            if (layerIndex++ == 0) {
                // Do not process input layer
                continue;
            }
            for (Neuron node : layer) {
                node.process();
            }
        }
        for (int i = 0; i < getOutputLayer().size(); i++) {
            output[i] = getOutputLayer().neurons.get(i).output;
        }
/*        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.getNeurons().get(i).output = 1
        }*/

        return output;
    }

    public NeuronLayer getLastLayer() {
        return getLayer(layers.size() - 1);
    }

    public static NeuralNetwork createNetwork(int... layerSizes) {
        if (layerSizes.length < 2) {
            throw new IllegalArgumentException("Network layers must be at least 2");
        }
        NeuralNetwork network = new NeuralNetwork();
        NeuronLayer layer = network.createLayer("INPUT");
        for (int i = 0; i < layerSizes[0]; i++) {
            layer.createNeuron();
        }

        int hiddenIndex = 1;
        for (int layerIndex = 1; layerIndex < layerSizes.length - 1; layerIndex++) {
            int layerSize = layerSizes[layerIndex];
            NeuronLayer parentLayer = layer;
            layer = network.createLayer("HIDDEN " + (hiddenIndex++));
            for (int i = 0; i < layerSize; i++) {
                layer.createNeuron().addInputs(parentLayer);
            }
        }

        NeuronLayer parentLayer = network.getLastLayer();
        layer = network.createLayer("OUTPUT");
        int outputNodes = layerSizes[layerSizes.length - 1];
        for (int i = 0; i < outputNodes; i++) {
            layer.createNeuron().addInputs(parentLayer);
        }

        return network;
    }
}
