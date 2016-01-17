package net.zomis.machlearn.neural

import java.util.stream.DoubleStream

class BackPropagation {

    final double learningRate
    final int iterations
    int logRate = Integer.MAX_VALUE

    public BackPropagation(double learningRate, int iterations) {
        assert 0 < learningRate
        assert learningRate < 1
        this.learningRate = learningRate
        this.iterations = iterations
    }

    static class Deltas {
        private final Map<Neuron, Integer> neuronIndexes = new HashMap<>()
        private final double[] deltaValues

        Deltas(NeuralNetwork network) {
            int index = 0
            for (NeuronLayer layer : network.getLayers()) {
                for (Neuron neuron : layer) {
                    neuronIndexes.put(neuron, index++)
                }
            }
            this.deltaValues = new double[index]
        }

        def set(Neuron neuron, double v) {
            deltaValues[neuronIndexes.get(neuron)] = v
        }

        double get(Neuron neuron) {
            deltaValues[neuronIndexes.get(neuron)]
        }

        def log() {
            println "Deltas are $deltaValues"
        }

        @Override
        String toString() {
            Arrays.toString(deltaValues)
        }
    }

    NeuralNetwork backPropagationLearning(Collection<LearningData> examples, NeuralNetwork network) {
        backPropagationLearning(examples, network, new Random())
    }

    NeuralNetwork backPropagationLearning(Collection<LearningData> examples, NeuralNetwork network, Random random) {
        int[] layerSizes = network.layers.stream().mapToInt({it.size()}).toArray()
//        inputs: examples, a set of examples, each with input vector x and output vector y
//        network , a multilayer network with L layers, weights wi,j , activation function g

        // local variables: Δ, a vector of errors, indexed by network node
        int iterations = 0

        network.links().forEach({it.weight = random.nextDouble()})
        double[][] deltas = new double[network.layerCount - 1][]
        for (int layeri = 0; layeri < network.layerCount - 1; layeri++) {
            NeuronLayer layer = network.getLayer(layeri + 1)
            deltas[layeri] = new double[layer.size()];
        }

        double[][][] capitalDeltas = new double[network.layerCount - 1][][]
        for (int layerIndex = 0; layerIndex < network.layerCount - 1; layerIndex++) {
            /* capitalDeltas[L][i][j] =
             * delta between layer L and layer L + 1, including input layer, excluding ooutput layer
             * node i in layer L+1
             * if j == 0, bias unit in layer L
             * otherwise, node j (1-indexed) in layer L
             **/
            NeuronLayer layer = network.getLayer(layerIndex + 1)
            capitalDeltas[layerIndex] = new double[layer.size()][]
            for (int i = 0; i < layer.neurons.size(); i++) {
                Neuron neuron = layer.neurons.get(i)
                capitalDeltas[layerIndex][i] = new double[neuron.inputs.size()]
            }
        }

        while (true) {
            zero(deltas) // TODO: Should the `deltas` array be zeroed here? Might not need zeroing
            zero(capitalDeltas)

            iterations++
            double cost = 0
            for (LearningData data : examples) {
                /* Propagate the inputs forward to compute the outputs */
                int neuronIndexInLayer = 0
                for (Neuron neuron : network.getInputLayer()) {
                    neuron.output = data.inputs[neuronIndexInLayer++]
                }
                for (int layerIndex = 1; layerIndex < network.getLayerCount(); layerIndex++) {
                    NeuronLayer layer = network.getLayer(layerIndex)
                    for (Neuron node : layer) {
                        node.process()
                    }
                }

                /* Using y(t) compute delta(L) = a(L) - y(t) */
                double[] expectedOutput = data.outputs
                neuronIndexInLayer = 0
                for (Neuron neuron : network.getOutputLayer()) {
                    double neuronError = neuron.output - expectedOutput[neuronIndexInLayer]
                    deltas[network.layerCount - 2][neuronIndexInLayer] = neuronError
                    neuronIndexInLayer++
                }

                /* Propagate deltas backward from output layer to input layer
                 * Compute delta(L-1), delta(L-2), ..., delta(2)
                  * using delta(l) = ((theta(l)' * delta(l+1) .* a(l) .* (1-a(l)) */
                for (int layerIndex = network.getLayerCount() - 2; layerIndex >= 1; layerIndex--) {
                    NeuronLayer layer = network.getLayer(layerIndex)
                    for (int nodei = 0; nodei < layer.size(); nodei++) {
                        Neuron neuron = layer.neurons.get(nodei)
                        double sum = neuron.outputs.stream().mapToDouble({
                            it.weight * deltas[layerIndex][it.to.indexInLayer]
                        }).sum()
                        double gPrim = neuron.output * (1 - neuron.output)
                        double delta = sum * gPrim
                        deltas[layerIndex - 1][nodei] = delta
                        // println "deltas[$layerIndex - 1][$nodei] = $sum * $gPrim = $delta"
                    }
                }

                /* capitalDelta(l, i, j) += a(l, j) * delta(l+1, i) */
                for (int layerIndex = 0; layerIndex < network.getLayerCount() - 1; layerIndex++) {
                    NeuronLayer layer = network.getLayer(layerIndex)
                    NeuronLayer nextLayer = network.getLayer(layerIndex + 1)
                    for (int i = 0; i < nextLayer.neurons.size(); i++) {
                        for (int j = 0; j < layer.neurons.size() + 1; j++) {
                            /* capitalDeltas[L][i][j] =
                             * delta between layer L and layer L + 1, including input layer, excluding ooutput layer
                             * node i in layer L+1
                             * if j == 0, bias unit in layer L
                             * otherwise, node j (1-indexed) in layer L
                             **/
                            double wantedDeltaValue = deltas[layerIndex][i]
                            double value = 1
                            if (j > 0) {
                                value = layer.neurons.get(j-1).output
                            }
                            capitalDeltas[layerIndex][i][j] += value * wantedDeltaValue
                        }
                    }
                }

                cost += costFunction(network, data)
            }

            double totalChange = 0
            /* Update every weight in network using deltas */
            for (int l = 0; l < capitalDeltas.length; l++) {
                NeuronLayer nextLayer = network.getLayer(l + 1)
                double totalLayerChange = 0
                for (int i = 0; i < capitalDeltas[l].length; i++) { // index in nextLayer
                    for (int j = 0; j < capitalDeltas[l][i].length; j++) { // index in `layer`, or 0 for bias unit
                        double regularization = 0
                        NeuronLink link = nextLayer.neurons.get(i).inputs.get(j)
                        double capitalD = 1d / examples.size() * (capitalDeltas[l][i][j] + regularization)
                        // double gradientCheck = gradientCheck(link, network, examples)
                        totalChange += Math.abs(capitalD)
                        totalLayerChange += Math.abs(capitalD)
                        // println "$gradientCheck vs $capitalD = ${Math.abs(gradientCheck - capitalD)}"

                        link.weight = link.weight - learningRate * capitalD
                    }
                }
                // println "Layer $l change $totalLayerChange"
            }
            // println "lowercs deltas $deltas"
            // println "capital deltas $capitalDeltas"
/*            for (int i = 0; i < capitalDeltas.length; i++) {
                for (int j = 0; j < capitalDeltas[i].length; j++) {
                    println "capitalDeltas[$i][$j] = ${capitalDeltas[i][j]}"
                }
                new Scanner(System.in).nextLine()
            }

            for (int i = 0; i < deltas.length; i++) {
                println "       deltas[$i] = ${deltas[i]}"
            }
            new Scanner(System.in).nextLine()

            println "total change $totalChange"*/

            double regularizationTerm = 0
            // println "cost before avg $cost"
            cost = (-cost / examples.size()) + regularizationTerm

            if (iterations % logRate == 0) {
                DoubleSummaryStatistics data = Arrays.asList(deltas).stream().flatMapToDouble({Arrays.stream(it)}).summaryStatistics()
                DoubleSummaryStatistics capitalDeltaData = Arrays.asList(capitalDeltas).stream()
                    .flatMap({Arrays.asList(it).stream()})
                    .flatMapToDouble({Arrays.stream(it)}).summaryStatistics()
                println "BackPropagation $layerSizes iteration $iterations : $data $capitalDeltaData cost $cost"
            }
            if (iterations > this.iterations) {
                break;
            }
        }
        return network
    }

    static double gradientCheck(NeuronLink link, NeuralNetwork network, Collection<LearningData> datas) {
        double originalWeight = link.weight
        final double EPSILON = 0.0001

        link.weight = originalWeight + EPSILON
        double costPlus = costFunction(network, datas)

        link.weight = originalWeight - EPSILON
        double costMinus = costFunction(network, datas)

        // println "Cost plus $costPlus cost minus $costMinus"
        link.weight = originalWeight

        return (costPlus - costMinus) / (2 * EPSILON)
    }

    static void zero(double[][] doubles) {
        for (double[] layer : doubles) {
            Arrays.fill(layer, 0)
        }
    }

    static void zero(double[][][] doubles) {
        for (double[][] layer : doubles) {
            zero(layer)
        }
    }

    static double costFunction(NeuralNetwork network, Collection<LearningData> datas) {
        double sum = 0
        for (LearningData data : datas) {
            network.run(data.inputs)
            sum += costFunction(network, data)
        }
        // println "costFunction sum $sum training set ${datas.size()}"
        return (-1d / datas.size()) * sum
    }

    static double costFunction(NeuralNetwork network, LearningData learningData) {
        double sum = 0
        double[] out = learningData.outputs

        NeuronLayer outputLayer = network.getOutputLayer()
        for (int i = 0; i < out.length; i++) {
            double expected = out[i]
            double actual = outputLayer.neurons.get(i).output
            sum += logisticCost(expected, actual)
        }
        sum
    }

    static double logisticCost(double expected, double actual) {
        expected * Math.log(actual) + (1 - expected) * Math.log(1 - actual)
    }
}
