package net.zomis.machlearn.neural

class NeuronLayer implements Iterable<Neuron> {

    List<Neuron> neurons = []
    String name

    NeuronLayer(String name) {
        this.name = name
    }

    Neuron createNeuron() {
        def neuron = new Neuron(name + '-' + neurons.size())
        this.neurons << neuron
        neuron
    }

    @Override
    Iterator<Neuron> iterator() {
        return neurons.iterator()
    }

    void printNodes() {
        println "LAYER"
        neurons.forEach({
            it.printInfo()
        })
        println()
    }

    int size() {
        neurons.size()
    }
}
