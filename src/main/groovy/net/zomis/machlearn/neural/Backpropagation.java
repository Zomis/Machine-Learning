package net.zomis.machlearn.neural;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;

public class Backpropagation {

    private final double learningRate;
    private final int iterations;
    private int logRate = Integer.MAX_VALUE;

    public Backpropagation(double learningRate, int iterations) {
        if (learningRate <= 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be in range (0..1]");
        }
        this.learningRate = learningRate;
        this.iterations = iterations;
    }

    public NeuralNetwork backPropagationLearning(Collection<LearningData> examples, NeuralNetwork network) {
        return backPropagationLearning(examples, network, initializeRandom(new Random()));
    }

    public static Consumer<NeuralNetwork> initializeRandom(Random random) {
        return network ->
            network.links().forEach(it -> it.setWeight(random.nextDouble() / 2 - 0.25));
    }

    public NeuralNetwork backPropagationLearning(Collection<LearningData> examples,
             NeuralNetwork network, Consumer<NeuralNetwork> weightsInitialization) {
        int[] layerSizes = network.getLayers().stream().mapToInt(it -> it.size()).toArray();
//        inputs: examples, a set of examples, each with input vector x and output vector y
//        network , a multilayer network with L layers, weights wi,j , activation function g

        // local variables: Δ, a vector of errors, indexed by network node
        int iterations = 0;

        if (weightsInitialization != null) {
            weightsInitialization.accept(network);
        }
        double[][] deltas = new double[network.getLayerCount() - 1][];
        for (int layeri = 0; layeri < network.getLayerCount() - 1; layeri++) {
            NeuronLayer layer = network.getLayer(layeri + 1);
            deltas[layeri] = new double[layer.size()];
        }

        double[][][] capitalDeltas = new double[network.getLayerCount() - 1][][];
        for (int layerIndex = 0; layerIndex < network.getLayerCount() - 1; layerIndex++) {
            /* capitalDeltas[L][i][j] =
             * delta between layer L and layer L + 1, including input layer, excluding ooutput layer
             * node i in layer L+1
             * if j == 0, bias unit in layer L
             * otherwise, node j (1-indexed) in layer L
             **/
            NeuronLayer layer = network.getLayer(layerIndex + 1);
            capitalDeltas[layerIndex] = new double[layer.size()][];
            for (int i = 0; i < layer.getNeurons().size(); i++) {
                Neuron neuron = layer.getNeurons().get(i);
                capitalDeltas[layerIndex][i] = new double[neuron.getInputs().size()];
            }
        }

        while (true) {
            zero(deltas); // TODO: Should the `deltas` array be zeroed here? Might not need zeroing
            zero(capitalDeltas);

            iterations++;
            double cost = 0;
            for (LearningData data : examples) {
                /* Propagate the inputs forward to compute the outputs */
                int neuronIndexInLayer = 0;
                for (Neuron neuron : network.getInputLayer()) {
                    neuron.setOutput(data.getInput(neuronIndexInLayer++));
                }
                for (int layerIndex = 1; layerIndex < network.getLayerCount(); layerIndex++) {
                    NeuronLayer layer = network.getLayer(layerIndex);
                    for (Neuron node : layer) {
                        node.process();
                    }
                }

                /* Using y(t) compute delta(L) = a(L) - y(t) */
                double[] expectedOutput = data.getOutputs();
                neuronIndexInLayer = 0;
                for (Neuron neuron : network.getOutputLayer()) {
                    // TODO: What happens if we ignore the output for some neurons when training some training sets?
                    double neuronError = neuron.getOutput() - expectedOutput[neuronIndexInLayer];
                    neuronError = neuronError * data.weight;
                    deltas[network.getLayerCount() - 2][neuronIndexInLayer] = neuronError;
                    neuronIndexInLayer++;
                }

                /* Propagate deltas backward from output layer to input layer
                 * Compute delta(L-1), delta(L-2), ..., delta(2)
                  * using delta(l) = ((theta(l)' * delta(l+1) .* a(l) .* (1-a(l)) */
                for (int layerIndex = network.getLayerCount() - 2; layerIndex >= 1; layerIndex--) {
                    final int layerIdx = layerIndex;
                    NeuronLayer layer = network.getLayer(layerIndex);
                    for (int nodei = 0; nodei < layer.size(); nodei++) {
                        Neuron neuron = layer.getNeurons().get(nodei);
                        double sum = neuron.getOutputs().stream().mapToDouble(link ->
                            link.getWeight() * deltas[layerIdx][link.getTo().indexInLayer]
                        ).sum();
                        double gPrim = neuron.getOutput() * (1 - neuron.getOutput());
                        double delta = sum * gPrim;
                        deltas[layerIndex - 1][nodei] = delta;
                        // println "deltas[$layerIndex - 1][$nodei] = $sum * $gPrim = $delta"
                    }
                }

                /* capitalDelta(l, i, j) += a(l, j) * delta(l+1, i) */
                for (int layerIndex = 0; layerIndex < network.getLayerCount() - 1; layerIndex++) {
                    NeuronLayer layer = network.getLayer(layerIndex);
                    NeuronLayer nextLayer = network.getLayer(layerIndex + 1);
                    for (int i = 0; i < nextLayer.getNeurons().size(); i++) {
                        for (int j = 0; j < layer.getNeurons().size() + 1; j++) {
                            /* capitalDeltas[L][i][j] =
                             * delta between layer L and layer L + 1, including input layer, excluding ooutput layer
                             * node i in layer L+1
                             * if j == 0, bias unit in layer L
                             * otherwise, node j (1-indexed) in layer L
                             **/
                            double wantedDeltaValue = deltas[layerIndex][i];
                            double value = 1;
                            if (j > 0) {
                                value = layer.getNeurons().get(j-1).getOutput();
                            }
                            capitalDeltas[layerIndex][i][j] += value * wantedDeltaValue;
                        }
                    }
                }

                cost += costFunction(network, data);
            }

            double totalChange = 0;
            /* Update every weight in network using deltas */
            for (int l = 0; l < capitalDeltas.length; l++) {
                NeuronLayer nextLayer = network.getLayer(l + 1);
                double totalLayerChange = 0;
                for (int i = 0; i < capitalDeltas[l].length; i++) { // index in nextLayer
                    for (int j = 0; j < capitalDeltas[l][i].length; j++) { // index in `layer`, or 0 for bias unit
                        double regularization = 0;
                        NeuronLink link = nextLayer.getNeurons().get(i).getInputs().get(j);
                        double capitalD = 1d / examples.size() * (capitalDeltas[l][i][j] + regularization);
                        // double gradientCheck = gradientCheck(link, network, examples)
                        totalChange += Math.abs(capitalD);
                        totalLayerChange += Math.abs(capitalD);
                        // println "$gradientCheck vs $capitalD = ${Math.abs(gradientCheck - capitalD)}"

                        link.setWeight(link.getWeight() - learningRate * capitalD);
                    }
                }
                // println "Layer $l change $totalLayerChange"
            }


            double regularizationTerm = 0;
            cost = (-cost / examples.size()) + regularizationTerm;

            if (iterations % logRate == 0) {
//                DoubleSummaryStatistics data = Arrays.asList(deltas).stream()
//                    .flatMapToDouble(Arrays::stream)
//                    .summaryStatistics();
//                DoubleSummaryStatistics capitalDeltaData = Arrays.asList(capitalDeltas).stream()
//                        .flatMap(it -> Arrays.asList(it).stream())
//                        .flatMapToDouble(Arrays::stream).summaryStatistics();
//                System.out.printf("BackPropagation %s iteration %d : %s %s cost %s%n",
//                        Arrays.toString(layerSizes), iterations, data, capitalDeltaData, cost);
                System.out.printf("BackPropagation %s iteration %d : change %f, cost %f%n",
                        Arrays.toString(layerSizes), iterations, totalChange, cost);
            }
            if (iterations > this.iterations) {
                break;
            }
        }
        return network;
    }

    static double gradientCheck(NeuronLink link, NeuralNetwork network, Collection<LearningData> datas) {
        double originalWeight = link.getWeight();
        final double EPSILON = 0.0001;

        link.setWeight(originalWeight + EPSILON);
        double costPlus = costFunction(network, datas);

        link.setWeight(originalWeight - EPSILON);
        double costMinus = costFunction(network, datas);

        // println "Cost plus $costPlus cost minus $costMinus"
        link.setWeight(originalWeight);

        return (costPlus - costMinus) / (2 * EPSILON);
    }

    static void zero(double[][] doubles) {
        for (double[] layer : doubles) {
            Arrays.fill(layer, 0);
        }
    }

    static void zero(double[][][] doubles) {
        for (double[][] layer : doubles) {
            zero(layer);
        }
    }

    static double costFunction(NeuralNetwork network, Collection<LearningData> datas) {
        double sum = 0;
        for (LearningData data : datas) {
            network.run(data.getInputs());
            sum += costFunction(network, data);
        }
        // println "costFunction sum $sum training set ${datas.size()}"
        return (-1d / datas.size()) * sum;
    }

    static double costFunction(NeuralNetwork network, LearningData learningData) {
        double sum = 0;
        double[] out = learningData.getOutputs();

        NeuronLayer outputLayer = network.getOutputLayer();
        for (int i = 0; i < out.length; i++) {
            double expected = out[i];
            double actual = outputLayer.getNeurons().get(i).getOutput();
            sum += logisticCost(expected, actual);
        }
        return sum;
    }

    static double logisticCost(double expected, double actual) {
        return expected * Math.log(actual) + (1 - expected) * Math.log(1 - actual);
    }

    public Backpropagation setLogRate(int logRate) {
        this.logRate = logRate;
        return this;
    }

    public int getLogRate() {
        return logRate;
    }

    public static Consumer<NeuralNetwork> initializeRandomOffset(Random random, double v) {
        return network ->
            network.links().forEach(it -> it.setWeight(it.getWeight() + random.nextDouble() * v * 2 - v));
    }
}
