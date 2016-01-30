package net.zomis.machlearn.images;

import net.zomis.combinatorics.Binero;
import net.zomis.combinatorics.IntegerPoints;
import net.zomis.machlearn.neural.Backpropagation;
import net.zomis.minesweeper.analyze.AnalyzeFactory;
import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.Solution;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BineroScan {

    public static void main(String[] args) {
        BufferedImage image = MyImageUtil.resource("binero.png");
        ZRect[][] boardRects = imageToRects(image);
        String board = valuesForBoard(image, boardRects);
        System.out.println(board);
        ByteArrayInputStream stream = new ByteArrayInputStream(board.getBytes(StandardCharsets.UTF_8));
        try {
            AtomicInteger size = new AtomicInteger(boardRects.length);
            AnalyzeFactory<Integer> analyzeFactory = Binero.binero(stream, size);
            if (size.get() != boardRects.length) {
                throw new IllegalStateException(size.get() + " vs. " + boardRects.length);
            }
            AnalyzeResult<Integer> analysis = analyzeFactory.solve();
            System.out.println(analysis);
            System.out.println(analysis.getRules());
            for (Solution<Integer> ee : analysis.getSolutions()) {
                System.out.println(ee);
                System.out.println(IntegerPoints.map(ee.getSetGroupValues(), size.get()));
                System.out.println("---");
            }            System.out.println(analysis.getSolutions());
            System.out.println(analysis.getTotal());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String valuesForBoard(BufferedImage image, ZRect[][] boardRects) {
        ImageNetwork network = constructRectToNumberNetwork();
        StringBuilder str = new StringBuilder();
        for (int y = 0; y < boardRects.length; y++) {
            for (int x = 0; x < boardRects[y].length; x++) {
                ZRect rect = boardRects[y][x];
                BufferedImage runImage = MinesweeperScan.scaledRunImage(network.getAnalysis(), image, rect);
                Map<Object, Double> values = network.run(network.imagePart(runImage, 0, 0));
                Map.Entry<Object, Double> best = values.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue)).get();
                if (best.getValue() > 0.5) {
                    str.append(best.getKey());
                } else {
                    str.append(' ');
                }
            }
            str.append('\n');
        }

        return str.toString();
    }

    private static ImageNetwork constructRectToNumberNetwork() {
        BufferedImage learningImage = MyImageUtil.resource("binero.png");
        ImageAnalysis analysis = new ImageAnalysis(30, 30, true);
        Backpropagation backprop = new Backpropagation(0.1, 2000).setLogRate(400);
        return analysis.neuralNetwork(20)
            .classify(0, analysis.imagePart(learningImage, 626, 76))
            .classify(1, analysis.imagePart(learningImage, 458, 76))
            .classifyNone(analysis.imagePart(learningImage, 593, 76))
            .learn(backprop, new Random(42));
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
        int[] xBoxes = createBox(xSeparatorLines);
        int[] yBoxes = createBox(ySeparatorLines);
        System.out.println(Arrays.toString(xBoxes));
        System.out.println(Arrays.toString(yBoxes));

        int height = yBoxes.length / 2;
        int width = xBoxes.length / 2;
        ZRect[][] result = new ZRect[height][width];
        for (int y = 0; y < height; y++) {
            int startY = yBoxes[y * 2];
            int endY = yBoxes[y * 2 + 1];
            for (int x = 0; x < width; x++) {
                int startX = xBoxes[x * 2];
                int endX = xBoxes[x * 2 + 1];
                ZRect rect = new ZRect();
                rect.left = startX;
                rect.right = endX;
                rect.top = startY;
                rect.bottom = endY;
                result[y][x] = rect;
            }
        }
        return result;
    }

    private static int[] createBox(List<Integer> values) {
        int[] diffsX = new int[values.size()];
        int old = 0;
        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            diffsX[i] = value - old;
            old = value;
        }
        int[] sortedX = Arrays.copyOf(diffsX, diffsX.length);
        Arrays.sort(sortedX);
        int squareSize = 0;
        for (int value : sortedX) {
            squareSize = value;
            if (squareSize > 1) {
                break;
            }
        }
        int firstIndex = 0;
        int lastIndex = diffsX.length - 1;
        while (Math.abs(diffsX[firstIndex] - squareSize) >= 2) firstIndex++;
        while (Math.abs(diffsX[lastIndex] - squareSize) >= 2) lastIndex--;

        List<Integer> result = new ArrayList<>();
        result.add(values.get(firstIndex - 1));
        for (int i = firstIndex; i <= lastIndex; i++) {
            if (diffsX[i] < squareSize) {
                continue;
            }
            int a = values.get(i);
            result.add(a);
            int skip = 0;
            while (diffsX.length > i + skip + 1 && diffsX[i + skip + 1] < squareSize) {
                skip++;
            }
            int b = values.get(i + skip);
            result.add(b);
            i += skip;
        }
        result.remove(result.size() - 1);

        return result.stream().mapToInt(i -> i).toArray();
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
