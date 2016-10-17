package net.zomis.qlearn;

import net.zomis.machlearn.neural.LearningData;
import net.zomis.machlearn.qlearn.OnlineLearningNetwork;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.CpuNDArrayFactory;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.List;

public class DL4jOnlineNetwork implements OnlineLearningNetwork {

    private final MultiLayerNetwork model;
    private CpuNDArrayFactory factory = new CpuNDArrayFactory();

    private static final double rate = 0.0015;
    private static final double learningRate = rate;


    public DL4jOnlineNetwork(int... layers) {
        NeuralNetConfiguration.ListBuilder conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .activation("relu")
                .weightInit(WeightInit.XAVIER)
                .learningRate(learningRate)
                .updater(Updater.SGD)//.momentum(0.98)
        //        .regularization(true).l2(rate * 0.005)
                .list();
        for (int i = 1; i < layers.length; i++) {
            FeedForwardLayer.Builder builder;
            if (i == layers.length - 1) {
                builder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .activation("softmax");
            } else {
                builder = new DenseLayer.Builder();
            }
            int in = layers[i - 1];
            int out = layers[i];
            conf = conf.layer(i - 1, builder
                .nIn(in)
                .nOut(out).build());
        }
        MultiLayerConfiguration result = conf.pretrain(false).backprop(true).build();
        model = new MultiLayerNetwork(result);
        model.init();
        model.setListeners(new ScoreIterationListener(1));
    }

    @Override
    public double[] run(double[] input) {
        INDArray array = factory.create(input);
        List<INDArray> results = model.feedForward(array, false);
        INDArray result = results.get(results.size() - 1);
        double[] arrayResult = result.data().asDouble();
        if (arrayResult.length != 4) {
            throw new AssertionError();
        }
        return arrayResult;
    }

    @Override
    public void learn(LearningData data) {
        INDArray input = factory.create(data.getInputs());
        INDArray labels = factory.create(data.getOutputs());
        DataSet ds = new DataSet(input, labels);
        model.fit(ds);
    }
}
