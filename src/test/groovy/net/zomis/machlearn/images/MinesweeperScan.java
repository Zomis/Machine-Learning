package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.Backpropagation;

import java.awt.image.BufferedImage;
import java.util.Random;

public class MinesweeperScan {

    public static void scan() {
        ImageAnalysis analysis = new ImageAnalysis(1, 100, true);
        String fileName = "challenge-flags-16x16.png";
        BufferedImage img = ImageUtil.resource(fileName);
        ImageNetwork network = analysis.neuralNetwork(40)
                .classifyNone(analysis.imagePart(img, 0, 540))
                .classifyNone(analysis.imagePart(img, 100, 540))
                .classifyNone(analysis.imagePart(img, 200, 540))
                .classifyNone(analysis.imagePart(img, 300, 540))
                .classifyNone(analysis.imagePart(img, 400, 540))
                .classifyNone(analysis.imagePart(img, 500, 540))
                .classifyNone(analysis.imagePart(img, 600, 540))
                .classifyNone(analysis.imagePart(img, 610, 540))
                .classify(true, analysis.imagePart(img, 625, 540))
                .classify(true, analysis.imagePart(img, 630, 540))
                .classify(true, analysis.imagePart(img, 670, 540))
                .learn(new Backpropagation(0.1, 10000), new Random(42));

        BufferedImage runImage = ImageUtil.resource("challenge-press-26x14.png");
        int x = runImage.getWidth() - 1;
        int y = runImage.getHeight() / 2;
        outer:
        while (true) {
            double[] input = analysis.imagePart(runImage, x, y);
            double[] output = network.getNetwork().run(input);
            for (double v : output) {
                if (v >= 0.7) {
                    break outer;
                }
            }
            x -= 3;
            System.out.println("x: " + x);
        }
        System.out.println("FINAL x: " + x);
    }

}
