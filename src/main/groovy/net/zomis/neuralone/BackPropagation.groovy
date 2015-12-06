package net.zomis.neuralone

class BackPropagation {

    private final Map<Neuron, Integer> neuronIndexes = new HashMap<>();

    private BackPropagation() {

    }


    def backPropagationLearning(Set<LearningData> examples, NeuralNetwork network) {
//        inputs: examples, a set of examples, each with input vector x and output vector y
//        network , a multilayer network with L layers, weights wi,j , activation function g

        // local variables: Δ, a vector of errors, indexed by network node
        def random = new Random(42)
        Map<Neuron, Integer> neuronIndexes = network.createIndexMap()

        while (true) {
            for (NeuronConnection link : network.getAllLinks()) {
                link.weight = random.nextFloat()
            }
            for (LearningData data : examples) {
                /* Propagate the inputs forward to compute the outputs */
                int index = 0
                for (Neuron neuron : network.inputs) {
                    neuron.output = data.inputs[index++]
                }
                for (int layerIndex = 2; layerIndex <= network.getLayerCount(); layerIndex++) {
                    List<Neuron> layer = network.getLayer(layerIndex)
                    for (Neuron node : layer) {
                        node.input = node.calculateInput()
                        node.output = node.calculateOutput(node.input)
                    }
                }

                /* Propagate deltas backward from output layer to input layer */
                float[] expectedOutput = data.outputs
                for (Neuron neuron : network.outputs) {
                    def neuronError = expectedOutput[neuron.index] - neuron.output
                    setDelta(neuron, gPrim(neuron.input) * neuronError)
                }

                for (int layerIndex = network.getLayerCount() - 1; layerIndex >= 1; layerIndex--) {
                    for (Neuron node : network.getLayer(layerIndex)) {
                        float sum = node.outputs.stream().mapToDouble({it.weight * getDelta(it.to)}).sum()
                        setDelta(node, gPrim(neuron.input) * sum)
                    }
                }

                /* Update every weight in network using deltas */
                for (NeuronConnection link : network.getAllLinks()) {
                    def learningRate = 0.2f
                    link.weight = link.weight + learningRate * link.from.output * getDelta(link.to)
                }
            }
            if (stopCriterionSatisfied) {
                break;
            }
        }
        return network
    }

}
