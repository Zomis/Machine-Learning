package net.zomis.machlearn.images;

import javafx.scene.image.Image;
import net.zomis.combinatorics.Binero;
import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.minesweeper.analyze.AnalyzeFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class BineroScan {

    public static void main(String[] args) {
        BufferedImage image = MyImageUtil.resource("binero.png");
        ZRect[][] boardRects = imageToRects(image);
        String board = valuesForBoard(image, boardRects);
        ByteArrayInputStream stream = new ByteArrayInputStream(board.getBytes(StandardCharsets.UTF_8));
        try {
            AtomicInteger size = new AtomicInteger();
            AnalyzeFactory<Integer> analyzeFactory = Binero.binero(stream, size);
            if (size.get() != boardRects.length) {
                throw new IllegalStateException(size.get() + " vs. " + boardRects.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String valuesForBoard(BufferedImage image, ZRect[][] boardRects) {
        return "";
    }

    private static ZRect[][] imageToRects(BufferedImage image) {
        ImageNetwork network = constructImageToRectsNetwork();

        int middleY = image.getHeight() / 2;
        List<Integer> xSeparatorLines = new ArrayList<>();
        for (int x = 0; x < image.getWidth(); x++) {
            double[] output = network.getNetwork().run(network.imagePart(image, x, middleY));
            if (output[0] > 0.6) {
                xSeparatorLines.add(x);
            }
        }
        System.out.println(xSeparatorLines);

        int middleX = xSeparatorLines.get(xSeparatorLines.size() / 2);

        List<Integer> ySeparatorLines = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            // running with the same network should work as we have the same sizes
            double[] output = network.getNetwork().run(ImageAnalysis.imagePart(image, middleX, y,
                    network.getHeight(), network.getWidth(), true)); // height and width has been flipped here
            if (output[0] > 0.6) {
                ySeparatorLines.add(y);
            }
        }
        System.out.println(ySeparatorLines);

        return createRectsFromLines(xSeparatorLines, ySeparatorLines);
    }

    public static ZRect[][] createRectsFromLines(List<Integer> xSeparatorLines, List<Integer> ySeparatorLines) {
        int[] values = xSeparatorLines.stream().mapToInt(i -> i).toArray();
        int[] diffsX = new int[xSeparatorLines.size()];
        int old = 0;
        for (int i = 0; i < xSeparatorLines.size(); i++) {
            int value = xSeparatorLines.get(i);
            diffsX[i] = value - old;
            old = value;
        }
        System.out.println(xSeparatorLines);
        System.out.println(Arrays.toString(diffsX));
        int[] sortedX = Arrays.copyOf(diffsX, diffsX.length);
        Arrays.sort(sortedX);
        int squareSize = 0;
        for (int value : sortedX) {
            squareSize = value;
            if (squareSize > 1) {
                break;
            }
        }
        System.out.println(Arrays.toString(sortedX));
        System.out.println(squareSize);
        int firstIndex = 0;
        int lastIndex = diffsX.length - 1;
        while (Math.abs(diffsX[firstIndex] - squareSize) >= 2) firstIndex++;
        while (Math.abs(diffsX[lastIndex] - squareSize) >= 2) lastIndex--;
        System.out.println(Arrays.toString(Arrays.copyOfRange(values, firstIndex - 1, lastIndex + 1)));

        List<Integer> result = new ArrayList<>();
        System.out.println("Add F: " + xSeparatorLines.get(firstIndex - 1));
        result.add(xSeparatorLines.get(firstIndex - 1));
        for (int i = firstIndex; i <= lastIndex; i++) {
            if (diffsX[i] < squareSize) {
                continue;
            }
            int a = xSeparatorLines.get(i);
            System.out.println("Add A: " + a);
            result.add(a);
            int skip = 0;
            while (diffsX[i + skip + 1] < squareSize) {
                skip++;
            }
            int b = xSeparatorLines.get(i + skip);
            System.out.println("Add B: " + b);
            result.add(b);
            i += skip;
            int diff = diffsX[i];
            System.out.println(diff);
        }
        System.out.println(result);

        return new ZRect[0][];
    }

    private static ImageNetwork constructImageToRectsNetwork() {
        ImageAnalysis analysis = new ImageAnalysis(1, 50, true);
        BufferedImage image = MyImageUtil.resource("binero.png");
        ZPoint yellow = new ZPoint(41, 400);
        ZPoint black1 = new ZPoint(47, 400);
        ZPoint black2 = new ZPoint(286, 400);
        ZPoint white = new ZPoint(100, 400);
        ZPoint[] negative = { yellow, white };
        ZPoint[] positive = { black1, black2 };
        ImageNetworkBuilder builder = analysis.neuralNetwork(5);
        for (ZPoint p : negative) {
            builder = builder.classifyNone(analysis.imagePart(image, p.getX(), p.getY()));
        }
        for (ZPoint p : positive) {
            builder = builder.classify(true, analysis.imagePart(image, p.getX(), p.getY()));
        }
        Backpropagation backprop = new Backpropagation(0.1, 10000);
        return builder.learn(backprop, new Random(42));
    }

}
