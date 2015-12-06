package net.zomis.neuralone

class NeuralNetwork {

    List<Neuron> inputs = []
    List<Neuron> outputs = []

    Neuron createOutputNeuron() {
        def neuron = new Neuron()
        neuron.outputs = null
        this.outputs << neuron
    }


    Neuron createInputNeuron() {
        def neuron = new Neuron()
        neuron.inputs = null
        neuron.function = {it}
        this.inputs << neuron
        neuron
    }
}
