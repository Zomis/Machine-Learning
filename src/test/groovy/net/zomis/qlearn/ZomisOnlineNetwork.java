package net.zomis.qlearn;

import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.neural.NeuralNetwork;
import net.zomis.machlearn.qlearn.OnlineLearningNetwork;

import java.util.Random;

public class ZomisOnlineNetwork implements OnlineLearningNetwork {

    private final NeuralNetwork network;
    private Backpropagation networkUpdate = new Backpropagation(0.1, 100);

    public ZomisOnlineNetwork(int... layers) {
        network = NeuralNetwork.createNetwork(layers);
        Backpropagation.initializeRandom(new Random(42)).accept(network);

    }

    @Override
    public double[] run(double[] input) {
        return network.run(input);
    }

    @Override
    public void learn(LearningData data) {
        networkUpdate.learnFromExample(network, data,
            Backpropagation.createDeltasArray(network), null);
    }
}
