package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.Backpropagation;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class MinesweeperScan {

    private static String LEARN_IMAGE = "challenge-flags-16x16.png";
    private static BufferedImage img = MyImageUtil.resource(LEARN_IMAGE);

    public static void scan() {
        ImageAnalysis analysis = new ImageAnalysis(1, 100, true);
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

        BufferedImage runImage = MyImageUtil.resource("challenge-press-26x14.png");
        ZRect rect = findEdges(network, runImage);
        System.out.println("Edges: " + rect);
        // also try find separations by scanning lines and finding the line with the lowest delta diff

        ZRect[][] gridLocations = findGrid(runImage, rect);
        char[][] gridValues = scanGrid(runImage, gridLocations);
        for (int y = 0; y < gridValues.length; y++) {
            for (int x = 0; x < gridValues[y].length; x++) {
                System.out.print(gridValues[y][x]);
            }
            System.out.println();
        }
    }

    private static char[][] scanGrid(BufferedImage runImage, ZRect[][] gridLocations) {
        String fileName = "challenge-flags-16x16.png";
        BufferedImage image = MyImageUtil.resource(fileName);
        ImageAnalysis analyze = new ImageAnalysis(36, 36, false);
        Map<Character, ZPoint> trainingSet = new HashMap<>();
        trainingSet.put('_', new ZPoint(622, 200));
        trainingSet.put('1', new ZPoint(793, 287));
        trainingSet.put('2', new ZPoint(665, 200));
        trainingSet.put('3', new ZPoint(793, 244));
        trainingSet.put('4', new ZPoint(750, 416));
        trainingSet.put('5', new ZPoint(664, 502));
        trainingSet.put('6', new ZPoint(707, 502));
        trainingSet.put('a', new ZPoint(793, 200));

        ImageNetworkBuilder networkBuilder = analyze.neuralNetwork(40);
        for (Map.Entry<Character, ZPoint> ee : trainingSet.entrySet()) {
            int yy = ee.getValue().getY();
            int xx = ee.getValue().getX();
            networkBuilder = networkBuilder.classify(ee.getKey(), analyze.imagePart(image, xx + 0, yy + 0));
//            for (int y = 4; y <= 4; y += 2) {
//                for (int x = 4; x <= 4; x += 2) {
//                    networkBuilder = networkBuilder.classify(ee.getKey(), analyze.imagePart(image, xx + x, yy + y));
//                }
//            }

        }
        ImageNetwork network = networkBuilder.classifyNone(analyze.imagePart(image, 0, 0))
                .classifyNone(analyze.imagePart(image, 878, 456))
                .classifyNone(analyze.imagePart(image, 903, 456))
                .classifyNone(analyze.imagePart(image, 948, 456))
                .classifyNone(analyze.imagePart(image, 1004, 558))
                .classifyNone(analyze.imagePart(image, 921, 496))
                .classifyNone(analyze.imagePart(image, 921, 536))
                .classifyNone(analyze.imagePart(image, 963, 536))
                .learn(new Backpropagation(0.1, 4000), new Random(42));

        char[][] result = new char[gridLocations.length][gridLocations[0].length];
        ImagePainter painter = new ImagePainter(runImage.getWidth(), runImage.getHeight());

        // MinesweeperScan.runOnImage(analyze, network, runImage, m -> m.values().stream().mapToDouble(d -> d).max().getAsDouble());
//        ImagePainter.visualizeNetwork(network, gridLocations[0][0].width(), gridLocations[0][0].height(), runImage,
//            scaledInput(gridLocations[0][0].width(), gridLocations[0][0].height()),
//            out -> Arrays.stream(out).max().getAsDouble()).save(new File("certainty-detailed.png"));

        for (int y = 0; y < gridLocations.length; y++) {
            for (int x = 0; x < gridLocations[y].length; x++) {
                ZRect rect = gridLocations[y][x];
                Map<Object, Double> output = findBestSquare(network, runImage, rect);
                double score = 0;
                if (output != null) {
                    double value = output.values().stream().mapToDouble(d -> d).max().getAsDouble();
                    score = value;
                    if (value >= 0.5) painter.drawRGB(rect, 0, value, 0);
                    else painter.drawRGB(rect, 1 - value, 0, 0);
                }
                char ch = charForOutput(output);
                System.out.printf("Square %d, %d was recognized as %s with score %f%n", x, y, ch, score);
                result[y][x] = ch;
            }
        }
        painter.save(new File("certainty.png"));
        return result;
    }

    private static Map<Object, Double> findBestSquare(ImageNetwork network, BufferedImage runImage, ZRect rect) {
        if (rect == null) {
            return null;
        }
        double bestScore = 0;
        Map<Object, Double> best = null;
        ZRect runRect = new ZRect();
        for (int y = rect.top; y < rect.top + rect.height() / 2; y += 2) {
            for (int x = rect.left; x < rect.left + rect.width() / 2; x += 2) {
                runRect.left = x;
                runRect.top = y;
                runRect.right = x + rect.width();
                runRect.bottom = y + rect.height();

                BufferedImage scaledRunImage = scaledRunImage(network, runImage, runRect);
                Map<Object, Double> map = network.run(network.imagePart(scaledRunImage, 0, 0));
                double score = map.values().stream().mapToDouble(d -> d).max().getAsDouble();
                if (score > bestScore) {
                    bestScore = score;
                    best = map;
                    System.out.printf("New best score %f result %s. Run on %s with real rect %s%n",
                        score, map, runRect, rect);
                }
            }
        }
        return best;
    }

    private static XYToDoubleArray scaledInput(int width, int height) {
        return new XYToDoubleArray() {
            @Override
            public double[] toInput(ImageNetwork network, BufferedImage image, int x, int y) {
                int min = Math.min(network.getWidth(), network.getHeight());
                int minRect = Math.min(width, height);
//                System.out.printf("x %d, y %d, width %d, height %d, MIN %d, MIN_RECT %d%n",
//                    x, y, width, height, min, minRect);
                BufferedImage cropped = Scalr.crop(image, x, y, minRect, minRect);
                BufferedImage run = Scalr.resize(cropped, min, min);
                return network.imagePart(run, 0, 0);
            }
        };
    }

    private static char charForOutput(Map<Object, Double> output) {
        if (output == null) {
            return '%';
        }
        Map.Entry<Object, Double> max = output.entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue())).get();
        if (max.getValue() < 0.5) {
            return '#';
        }
        return (Character) max.getKey();
    }

    private static BufferedImage scaledRunImage(ImageNetwork network, BufferedImage runImage, ZRect rect) {
        if (rect == null) {
            return null;
        }
        int min = Math.min(network.getWidth(), network.getHeight());
        int minRect = Math.min(rect.width(), rect.height());
        BufferedImage image = Scalr.crop(runImage, rect.left, rect.top, minRect, minRect);
        return Scalr.resize(image, min, min);
//        System.out.printf("Running on %s with target size %d, %d run image is %d, %d%n", rect,
//                network.getWidth(), network.getHeight(), run.getWidth(), run.getHeight());
    }

    private static ZRect[][] findGrid(BufferedImage runImage, ZRect rect) {
        // Classify the line separator as true
        ImageAnalysis horizontalAnalysis = new ImageAnalysis(50, 2, true);
        ImageNetwork horizontal = horizontalAnalysis.neuralNetwork(20)
                .classify(true, horizontalAnalysis.imagePart(img, 600, 235))
                .classify(true, horizontalAnalysis.imagePart(img, 700, 235))
                .classifyNone(horizontalAnalysis.imagePart(img, 600, 249))
                .classifyNone(horizontalAnalysis.imagePart(img, 664, 249))
                .learn(new Backpropagation(0.1, 10000), new Random(42));

        ImageAnalysis verticalAnalysis = new ImageAnalysis(2, 50, true);
        ImageNetwork vertical = verticalAnalysis.neuralNetwork(20)
                .classify(true, verticalAnalysis.imagePart(img, 700, 300))
                .classify(true, verticalAnalysis.imagePart(img, 700, 400))
                .classifyNone(verticalAnalysis.imagePart(img, 682, 279))
                .classifyNone(verticalAnalysis.imagePart(img, 765, 279))
                .classifyNone(verticalAnalysis.imagePart(img, 630, 249))
                .classifyNone(verticalAnalysis.imagePart(img, 795, 290))
                .classifyNone(verticalAnalysis.imagePart(img, 795, 365))
                .classifyNone(verticalAnalysis.imagePart(img, 795, 465))
                .classifyNone(verticalAnalysis.imagePart(img, 722, 497))
                .classifyNone(verticalAnalysis.imagePart(img, 770, 249))
                .classifyNone(verticalAnalysis.imagePart(img, 719, 497))
                .learn(new Backpropagation(0.1, 10000), new Random(42));

        List<Integer> horizontalLines = new ArrayList<>();
        for (int y = rect.top; y + horizontalAnalysis.getHeight() < rect.bottom; y++) {
            double[] input = horizontalAnalysis.imagePart(runImage, rect.left + 10, y);
            double[] output = horizontal.getNetwork().run(input);
            double result = output[0];
            if (result > 0.7) {
                horizontalLines.add(y);
            }
        }

        List<Integer> verticalLines = new ArrayList<>();
        for (int x = rect.left; x + verticalAnalysis.getWidth() < rect.right; x++) {
            double[] input = verticalAnalysis.imagePart(runImage, x, rect.top + 10);
            double[] output = vertical.getNetwork().run(input);
            double result = output[0];
            if (result > 0.7) {
                verticalLines.add(x);
            }
        }

//        runAndSave(verticalAnalysis, vertical, runImage);

        System.out.println("Edges: " + rect);
        System.out.println("Horizontal: " + horizontalLines);
        System.out.println("Vertical  : " + verticalLines);

        horizontalLines = removeCloseValues(horizontalLines, 15);
        verticalLines = removeCloseValues(verticalLines, 15);
        System.out.println("------------");
        System.out.println("Horizontal " + horizontalLines.size() + ": " + horizontalLines);
        System.out.println("Vertical " + verticalLines.size() + ": " + verticalLines);

        // Remove outliers
        int squareWidth = verticalLines.get(1) - verticalLines.get(0);
        int squareHeight = horizontalLines.get(1) - horizontalLines.get(0);
        verticalLines = removeCloseValues(verticalLines, (int) (squareWidth * 0.75));
        horizontalLines = removeCloseValues(horizontalLines, (int) (squareHeight * 0.75));

        System.out.println("------------");
        System.out.println("Horizontal " + horizontalLines.size() + ": " + horizontalLines);
        System.out.println("Vertical " + verticalLines.size() + ": " + verticalLines);

        ZRect[][] gridLocations = grabRects(runImage, rect, horizontalLines, verticalLines, squareWidth, squareHeight);
        System.out.println("Square size = " + squareWidth + " x " + squareHeight);
        System.out.println("Squares found: " + gridLocations[0].length + " x " + gridLocations.length);
        return gridLocations;
    }

    private static ZRect[][] grabRects(BufferedImage image, ZRect rect, List<Integer> horizontalLines, List<Integer> verticalLines,
             int squareWidth, int squareHeight) {
        horizontalLines = new ArrayList<>(horizontalLines);
        verticalLines = new ArrayList<>(verticalLines);
        horizontalLines.add(rect.top);
        horizontalLines.add(rect.bottom);
        verticalLines.add(rect.left);
        verticalLines.add(rect.right);
        Collections.sort(horizontalLines);
        Collections.sort(verticalLines);

        horizontalLines = removeCloseValues(horizontalLines, (int) (squareHeight * 0.75));
        verticalLines = removeCloseValues(verticalLines, (int) (squareWidth * 0.75));

//        int beforeFirstX = verticalLines.get(0) - rect.left;
//        int afterLastX = rect.right - verticalLines.get(verticalLines.size() - 1);
//        int beforeFirstY = horizontalLines.get(0) - rect.top;
//        int afterLastY = rect.bottom - horizontalLines.get(horizontalLines.size() - 1);

        System.out.println("Horizontal " + horizontalLines.size() + ": " + horizontalLines);
        System.out.println("Vertical " + verticalLines.size() + ": " + verticalLines);

        ZRect[][] results = new ZRect[horizontalLines.size() + 1][verticalLines.size() + 1];
        int x = 0;
        for (Integer left : verticalLines) {
            int y = 0;
            for (Integer top : horizontalLines) {
                ZRect r = new ZRect();
                r.left = left;
                r.top = top;
                r.right = left + squareWidth;
                r.bottom = top + squareHeight;
                if (r.right >= image.getWidth()) {
                    continue;
                }
                if (r.bottom >= image.getHeight()) {
                    continue;
                }
                results[y][x] = r;
                y++;
            }
            x++;
        }

        return results;
    }

    private static List<Integer> removeCloseValues(List<Integer> values, int closeRange) {
        List<Integer> result = new ArrayList<>();
        Integer last = null;
        for (Integer i : values) {
            if (last == null || last + closeRange < i) {
                last = i;
                result.add(i);
            }
        }
        return result;
    }

    private static void runAndSave(ImageNetwork network, BufferedImage image) {
        ImagePainter[] networkResult = runOnImage(network, image);
        for (int i = 0; i < networkResult.length; i++) {
            MyImageUtil.save(networkResult[i].getImage(), new File("network-result-" + i + ".png"));
        }
    }

    private static ImagePainter[] runOnImage(ImageNetwork network, BufferedImage runImage) {
        int maxY = runImage.getHeight() - network.getHeight();
        int maxX = runImage.getWidth() - network.getWidth();
        ImagePainter[] images = new ImagePainter[network.getNetwork().getOutputLayer().size()];
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImagePainter(runImage.getWidth(), runImage.getHeight());
        }

        for (int y = 0; y < maxY; y++) {
            if (y % 20 == 0) {
                System.out.println("process y " + y);
            }
            for (int x = 0; x < maxX; x++) {
                double[] input = network.imagePart(runImage, x, y);
                double[] output = network.getNetwork().run(input);
                for (int i = 0; i < output.length; i++) {
                    double value = output[i];
//                    System.out.println(x + ", " + y + ": " + grayscaleValue + " -- " + value);
                    images[i].drawGrayscale(x, y, value);
                }
            }
        }
        return images;
    }

    private static ZRect findEdges(ImageNetwork network, BufferedImage runImage) {
        ZRect rect = new ZRect();
        int x = runImage.getWidth() - 1;
        int y = runImage.getHeight() / 2;

        rect.left  = findEdge(network, runImage, 0, y, 3, 0).getX();
        rect.right = findEdge(network, runImage, x, y, -3, 0).getX();

        int imgBottom = runImage.getHeight() - network.getHeight();
        rect.top    = findEdge(network, runImage, rect.left, 0, 0, 3).getY();
        rect.bottom = findEdge(network, runImage, rect.left, imgBottom, 0, -3).getY()
            + network.getHeight();

        return rect;
    }

    private static ZPoint findEdge(ImageNetwork network,
           BufferedImage runImage,
           int x, int y, int deltaX, int deltaY) {
        while (true) {
            double[] input = network.imagePart(runImage, x, y);
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
