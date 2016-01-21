package net.zomis.machlearn.neural;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NeuronLayer implements Iterable<Neuron> {

    public final List<Neuron> neurons = new ArrayList<>();
    public final String name;

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public NeuronLayer(String name) {
        this.name = name;
    }

    public Neuron createNeuron() {
        Neuron neuron = new Neuron(neurons.size(), name + '-' + neurons.size());
        this.neurons.add(neuron);
        return neuron;
    }

    @Override
    public Iterator<Neuron> iterator() {
        return neurons.iterator();
    }

    public void printNodes() {
        System.out.println("LAYER");
        neurons.forEach(Neuron::printInfo);
        System.out.println();
    }

    public int size() {
        return neurons.size();
    }
}
