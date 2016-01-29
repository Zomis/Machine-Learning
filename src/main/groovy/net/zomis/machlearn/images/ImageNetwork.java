package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.NeuralNetwork;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImageNetwork {

    private final NeuralNetwork network;
    private final Object[] outputs;
    private final ImageAnalysis analysis;

    public ImageNetwork(ImageAnalysis analysis, NeuralNetwork network, Object[] outputs) {
        if (network.getOutputLayer().size() != outputs.length) {
            throw new IllegalArgumentException(
                    String.format("Network output layer (%d) does not match object array length (%d)",
                            network.getOutputLayer().size(), outputs.length));
        }
        this.analysis = analysis;
        this.network = network;
        this.outputs = Arrays.copyOf(outputs, outputs.length);
    }

    public Object[] getOutputs() {
        return Arrays.copyOf(outputs, outputs.length);
    }

    public Map<Object, Double> run(double[] imageData) {
        double[] outputs = network.run(imageData);
        Map<Object, Double> result = new HashMap<>();
        for (int i = 0; i < outputs.length; i++) {
            result.put(this.outputs[i], outputs[i]);
        }
        return result;
    }

    public NeuralNetwork getNetwork() {
        return this.network;
    }

    public Object getObject(int index) {
        return outputs[index];
    }

    public int getWidth() {
        return analysis.getWidth();
    }

    public int getHeight() {
        return analysis.getHeight();
    }

    public double[] imagePart(BufferedImage image, int x, int y) {
        return analysis.imagePart(image, x, y);
    }

}
