package net.zomis.neuralone

class NeuralMain {

    public static void main(String[] args) {
        def network = new NeuralNetwork()
        def a = new Neuron()
        def b = new Neuron()
        def outA = network.createOutputNeuron()
        def outB = network.createOutputNeuron()
        a.addOutput(outA).addOutput(outB)
        b.addOutput(outA).addOutput(outB)

        network.createInputNeuron()
            .addOutput(a)
            .addOutput(b)
        network.createInputNeuron()
            .addOutput(a)
            .addOutput(b)


    }

}
