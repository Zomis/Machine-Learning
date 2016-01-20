package net.zomis.machlearn.neural

class NeuralMain {

    public static void main(String[] args) {
        def network = new NeuralNetwork()

        def inputLayer = network.createLayer('IN')
        inputLayer.createNeuron()
        inputLayer.createNeuron()

        def middleLayer = network.createLayer('MIDDLE')
        middleLayer.createNeuron().addInputs(inputLayer)
        middleLayer.createNeuron().addInputs(inputLayer)

        def outputLayer = network.createLayer('OUT')
        outputLayer.createNeuron().addInputs(middleLayer)
        outputLayer.createNeuron().addInputs(middleLayer)

        List<LearningData> examples = []
        examples << new LearningData([0, 0] as double[], [0, 0] as double[])
        examples << new LearningData([0, 1] as double[], [0, 1] as double[])
        examples << new LearningData([1, 0] as double[], [0, 1] as double[])
        examples << new LearningData([1, 1] as double[], [1, 1] as double[])
        new Backpropagation(0.2, 100000).backPropagationLearning(examples, network)

        network.printAll()

        for (LearningData data : examples) {
            def output = network.run(data.inputs)
            println "$data.inputs --> $output"
            network.printAll()
            println "-----------------"
        }
    }

}
