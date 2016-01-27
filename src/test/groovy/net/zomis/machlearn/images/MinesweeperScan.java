package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.Backpropagation;

import java.awt.*;
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
        ZRect rect = findEdges(network, analysis, runImage);
        System.out.println("Edges: " + rect);
    }

    private static ZRect findEdges(ImageNetwork network, ImageAnalysis analysis, BufferedImage runImage) {
        ZRect rect = new ZRect();
        int x = runImage.getWidth() - 1;
        int y = runImage.getHeight() / 2;

        rect.left  = findEdge(network, analysis, runImage, 0, y, 3, 0).getX();
        rect.right = findEdge(network, analysis, runImage, x, y, -3, 0).getX();

        int imgBottom = runImage.getHeight() - analysis.getHeight();
        rect.top    = findEdge(network, analysis, runImage, rect.left, 0, 0, 3).getY();
        rect.bottom = findEdge(network, analysis, runImage, rect.left, imgBottom, 0, -3).getY()
            + analysis.getHeight();

        return rect;
    }

    private static ZPoint findEdge(ImageNetwork network,
           ImageAnalysis analysis, BufferedImage runImage,
           int x, int y, int deltaX, int deltaY) {
        while (true) {
            double[] input = analysis.imagePart(runImage, x, y);
            double[] output = network.getNetwork().run(input);
            for (double v : output) {
                if (v >= 0.7) {
                    return new ZPoint(x, y);
                }
            }
            x += deltaX;
            y += deltaY;
            if (!inRange(x, y, runImage)) {
                throw new RuntimeException("Unable to find goal");
            }
        }
    }

    private static boolean inRange(int x, int y, BufferedImage img) {
        return x >= 0 &&
            y >= 0 &&
            x < img.getWidth() &&
            y < img.getHeight();
    }

}
