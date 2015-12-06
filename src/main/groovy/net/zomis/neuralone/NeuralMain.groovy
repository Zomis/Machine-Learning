package net.zomis.neuralone

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

        Set<LearningData> examples = new HashSet<LearningData>()
        examples << new LearningData([0, 0] as double[], [0, 0] as double[])
        examples << new LearningData([0, 1] as double[], [0, 1] as double[])
        examples << new LearningData([1, 0] as double[], [0, 1] as double[])
        examples << new LearningData([1, 1] as double[], [1, 1] as double[])
        BackPropagation.backPropagationLearning(examples, network)

        network.printAll()

        // Deltas are [-0.001254393261558168, -0.001669496114341458, -0.011844828972942834, 0.014466386462268437, -0.1438964290513059, 0.06944575122635648]
        println network.run([0, 0] as double[])
        println network.run([0, 1] as double[])
        println network.run([1, 0] as double[])
        println network.run([1, 1] as double[])
        network.printAll()
    }

}
