package net.zomis.machlearn.images;

import net.zomis.machlearn.neural.Backpropagation;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Minesweeper pipeline:
 * - Scan for grid edges
 * - Scan for square separators, construct ZRect[][]
 * - Scan each square to determine the value
 */
public class MinesweeperScan {

    private static final boolean SAVE_ERROR_SQUARES = true;

    private static String LEARN_IMAGE = "minesweeper/challenge-flags-16x16.png";
    private static BufferedImage img = MyImageUtil.resource(LEARN_IMAGE);
    private static double THRESHOLD = 0.3d;

    private final ImageNetwork edgeFind;
    private final ImageNetwork squareRecognition;
    private final ImageNetwork vertical;
    private final ImageNetwork horizontal;

    public MinesweeperScan() {
        Backpropagation fastBackprop = new Backpropagation(0.1, 10000);
        fastBackprop.setLogRate(1000);
        ImageAnalysis horizontalAnalysis = new ImageAnalysis(50, 1, true);
        ImageNetworkBuilder horizontal = horizontalAnalysis.neuralNetwork(20)
                .classify(true, horizontalAnalysis.imagePart(img, 600, 235))
                .classify(true, horizontalAnalysis.imagePart(img, 700, 235))
                .classifyNone(horizontalAnalysis.imagePart(img, 600, 249))
                .classifyNone(horizontalAnalysis.imagePart(img, 664, 249));
        classifyRange(horizontal, true, img, new ZRect(600, 195, 600 + 750, 195), 5, 1);
        this.horizontal = horizontal.learn(fastBackprop, new Random(42));

        ImageAnalysis verticalAnalysis = new ImageAnalysis(1, 50, true);
        vertical = verticalAnalysis.neuralNetwork(20)
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
                .learn(fastBackprop, new Random(42));

        ImageAnalysis analysis = new ImageAnalysis(1, 100, true);
        this.edgeFind = analysis.neuralNetwork(40)
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
                .learn(fastBackprop, new Random(42));


        String fileName = LEARN_IMAGE;
        BufferedImage image = MyImageUtil.resource(fileName);
        ImageAnalysis analyze = new ImageAnalysis(36, 36, false);
        Map<Character, ZPoint> trainingSet = new HashMap<>();
        trainingSet.put('_', new ZPoint(622, 200));
        // trainingSet.put('0', new ZPoint(964, 327));
        trainingSet.put('1', new ZPoint(793, 287));
        trainingSet.put('2', new ZPoint(665, 200));
        trainingSet.put('3', new ZPoint(793, 244));
        trainingSet.put('4', new ZPoint(750, 416));
        trainingSet.put('5', new ZPoint(664, 502));
        trainingSet.put('6', new ZPoint(707, 502));
        trainingSet.put('a', new ZPoint(793, 200));

        Backpropagation slowBackprop = new Backpropagation(0.1, 4000);
        slowBackprop.setLogRate(100);
        ImageNetworkBuilder networkBuilder = analyze.neuralNetwork(40);
        for (Map.Entry<Character, ZPoint> ee : trainingSet.entrySet()) {
            int yy = ee.getValue().getY();
            int xx = ee.getValue().getX();
//            for (int y = 4; y <= 4; y += 2) {
//                for (int x = 4; x <= 4; x += 2) {
//                    networkBuilder = networkBuilder.classify(ee.getKey(), analyze.imagePart(image, xx + x, yy + y));
//                }
//            }
            BufferedImage trainImage = Scalr.crop(image, xx, yy, analyze.getWidth(), analyze.getHeight());
            if (analyze.isGrayscale()) {
                trainImage = MyImageUtil.grayscale(trainImage);
            }
            MyImageUtil.save(trainImage, "train-" + ee.getKey());
            networkBuilder = networkBuilder.classify(ee.getKey(), analyze.imagePart(image, xx + 0, yy + 0));
        }
        ImageNetworkBuilder squareNetworkBuilder = networkBuilder.classifyNone(analyze.imagePart(image, 0, 0))
                .classifyNone(analyze.imagePart(image, 878, 456))
                .classifyNone(analyze.imagePart(image, 903, 456))
                .classifyNone(analyze.imagePart(image, 948, 456))
                .classifyNone(analyze.imagePart(image, 1004, 558))
                .classifyNone(analyze.imagePart(image, 921, 496))
                .classifyNone(analyze.imagePart(image, 921, 536))
                .classifyNone(analyze.imagePart(image, 963, 536));
        squareRecognition = squareNetworkBuilder.learn(slowBackprop, new Random(42));
//        squareRecognition = learnFromMinesweeperBoard(MinesweeperTrainingBoard.fromResource("challenge-flags-16x16"));

    }

    private static ImageNetworkBuilder classifyRange(ImageNetworkBuilder builder, Object object, BufferedImage img, ZRect rect,
                                              int windowStepX, int windowStepY) {
        for (int y = rect.top; y <= rect.bottom; y += windowStepY) {
            for (int x = rect.left; x <= rect.right; x += windowStepX) {
                builder = builder.classify(object, builder.getAnalysis().imagePart(img, x, y));
            }
        }
        return builder;
    }

    private ImageNetwork learnFromMinesweeperBoard(MinesweeperTrainingBoard minesweeperTrainingBoard) {
        BufferedImage image = minesweeperTrainingBoard.getImage();
        ZRect rect = findEdges(edgeFind, image);
        ZRect[][] gridLocations = findGrid(image, rect);
        int width = gridLocations[0][0].width();
        int height = gridLocations[0][0].height();
        int minWH = Math.min(width, height);
        ImageAnalysis analysis = new ImageAnalysis(minWH, minWH, false);
        ImageNetworkBuilder builder = analysis.neuralNetwork(40);

        String[] expectedRows = minesweeperTrainingBoard.getExpected().split("\n");
        Set<Character> trainingChars = new HashSet<>();
        for (int y = 0; y < expectedRows.length; y++) {
            String expectedRow = expectedRows[y].trim();
            for (int x = 0; x < expectedRow.length(); x++) {
                char expectedChar = expectedRow.charAt(x);
                if (trainingChars.add(expectedChar)) {
                    ZRect runRect = gridLocations[y][x];
                    BufferedImage runImage = scaledRunImage(analysis, image, runRect);
                    MyImageUtil.save(runImage, String.format("train-%d,%d-%s", x, y, expectedChar));
                    builder = builder.classify(expectedChar, analysis.imagePart(runImage, 0, 0));
                }
            }
        }

        Backpropagation backprop = new Backpropagation(0.1, 5000);
        backprop.setLogRate(200);
        return builder.learn(backprop, new Random(42));
    }

    public void scan(MinesweeperTrainingBoard board) {
        ZRect rect = findEdges(edgeFind, board.getImage());
        ImagePainter rectDraw = new ImagePainter(board.getImage());
        rectDraw.drawRect(rect, new Color(1, 1, 1, 0.6f));
        rectDraw.save("rect");
        System.out.println("Edges: " + rect);
        // also try find separations by scanning lines and finding the line with the lowest delta diff

        ImagePainter.visualizeNetwork(horizontal, horizontal.getWidth(), horizontal.getHeight(),
                board.getImage(), ImagePainter::normalInput, out -> out[0]).save("horizontal");

        ImagePainter.visualizeNetwork(vertical, vertical.getWidth(), vertical.getHeight(),
                board.getImage(), ImagePainter::normalInput, out -> out[0]).save("vertical");

        ZRect copyRect = new ZRect(rect);
        copyRect.expand(20);

        ZRect[][] gridLocations = findGrid(board.getImage(), copyRect);
        ImagePainter painter = new ImagePainter(board.getImage());
        painter.drawGrids(gridLocations);
        painter.save("grids");

        char[][] gridValues = scanGrid(board.getImage(), gridLocations, board.getExpected());
        for (int y = 0; y < gridValues.length; y++) {
            for (int x = 0; x < gridValues[y].length; x++) {
                System.out.print(gridValues[y][x]);
            }
            System.out.println();
        }
    }

    private char[][] scanGrid(BufferedImage runImage, ZRect[][] gridLocations, String expected) {
        char[][] result = new char[gridLocations.length][gridLocations[0].length];
        ImagePainter painter = new ImagePainter(runImage);

        // MinesweeperScan.runOnImage(analyze, network, runImage, m -> m.values().stream().mapToDouble(d -> d).max().getAsDouble());
//        ImagePainter[] painters = ImagePainter.visualizeNetworks(network, gridLocations[0][0].width(), gridLocations[0][0].height(), runImage,
//                scaledInput(gridLocations[0][0].width(), gridLocations[0][0].height()));
//        for (int i = 0; i < painters.length; i++) {
//            painters[i].save(new File("output-" + network.getOutputs()[i] + ".png"));
//        }

        String[] expectedRows = expected == null ? null : expected.split("\n");
        int wrongAnswers = 0;
        int checkedAnswers = 0;
        for (int y = 0; y < gridLocations.length; y++) {
            String expectedRow = expectedRows == null || expectedRows.length <= y ? null : expectedRows[y];
            expectedRow = expectedRow == null ? null : expectedRow.trim();
            for (int x = 0; x < gridLocations[y].length; x++) {
                ZRect rect = gridLocations[y][x];
                SquareRunResult output = rect == null ? null : findBestSquare(squareRecognition, runImage, rect,
                    rect.width() / 4, rect.height() / 4);
                double score = 0;
                if (output != null) {
                    double value = output.getBestScore();
                    score = value;
                    if (value >= THRESHOLD) painter.drawRGB(rect, 0, value, 0);
                    else painter.drawRGB(rect, 1 - value, 0, 0);
                }
                char ch = charForOutput(output == null ? null : output.getResult());
                result[y][x] = ch;
                if (expectedRow != null && expectedRow.length() > x) {
                    char expectedChar = expectedRow.charAt(x);
                    wrongAnswers += (expectedChar != ch) ? 1 : 0;
                    checkedAnswers++;
                    if (expectedChar != ch) {
                        Map.Entry<Object, Double> expectedEntry = output == null ? null : output.getResult().entrySet().stream()
                                .filter(e -> String.valueOf(e.getKey()).charAt(0) == expectedChar)
                                .findFirst().orElse(null);
                        System.out.printf("Square %d, %d was %s but expected %s. Score was %f vs expected %f%n",
                            x, y, ch, expectedChar, score, expectedEntry == null ? 0d : expectedEntry.getValue());
                        if (output != null && SAVE_ERROR_SQUARES) {
                            String saveFileName = String.format("best-%d-%d--%s-%f", x, y, ch, output.getBestScore());
                            MyImageUtil.save(output.getBestImage(), saveFileName);
                        }
                    }
                }
            }
        }
        System.out.printf("Wrong answers: %d of %d%n", wrongAnswers, checkedAnswers);
        painter.save("certainty");
        return result;
    }

    private static SquareRunResult findBestSquare(ImageNetwork network, BufferedImage runImage, ZRect rect,
              int windowWidth, int windowHeight) {
        if (rect == null) {
            return null;
        }
        double bestScore = 0;
        Map<Object, Double> best = null;
        BufferedImage bestImage = null;
        ZRect bestRect = null;
        ZRect runRect = new ZRect();
        for (int y = rect.top; y < rect.top + windowHeight; y += 2) {
            for (int x = rect.left; x < rect.left + windowWidth; x += 2) {
                runRect.left = x;
                runRect.top = y;
                runRect.right = x + rect.width();
                runRect.bottom = y + rect.height();

                BufferedImage scaledRunImage = scaledRunImage(network.getAnalysis(), runImage, runRect);
                Map<Object, Double> map = network.run(network.imagePart(scaledRunImage, 0, 0));
                double score = map.values().stream().mapToDouble(d -> d).max().getAsDouble();
                if (score > bestScore) {
                    bestScore = score;
                    best = map;
                    bestImage = scaledRunImage;
                    bestRect = runRect;
                    runRect = new ZRect();
//                    System.out.printf("New best score %f result %s. Run on %s with real rect %s%n",
//                        score, map, runRect, rect);
                }
            }
        }
//        Map.Entry<Object, Double> bestEntry = best.entrySet().stream()
//            .max(Comparator.comparingDouble(Map.Entry::getValue)).get();
//        MyImageUtil.save(bestImage, String.format("best-%d,%d-%s-%f", rect.left, rect.top,
//            bestEntry.getKey(), bestEntry.getValue()));
        return new SquareRunResult(best, bestImage, bestRect);
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
        if (max.getValue() < THRESHOLD) {
            return '#';
        }
        return (Character) max.getKey();
    }

    public static BufferedImage scaledRunImage(ImageAnalysis network, BufferedImage runImage, ZRect rect) {
        if (rect == null) {
            return null;
        }
        int min = Math.min(network.getWidth(), network.getHeight());
        int minRect = Math.min(rect.width(), rect.height());
        BufferedImage image = Scalr.crop(runImage, rect.left, rect.top, minRect, minRect);
        return Scalr.resize(image, min, min);
    }

    private ZRect[][] findGrid(BufferedImage runImage, ZRect rect) {
        double THRESHOLD = 0.5;

        // Classify the line separator as true
        List<Integer> horizontalLines = new ArrayList<>();
        boolean wasLine = true;
        for (int y = rect.top; y < rect.bottom; y++) {
            final int yy = y;
            IntStream stream = IntStream.range(0, rect.width() / 3);
            DoubleStream results = stream.<double[]>mapToObj(i -> horizontal.imagePart(runImage, rect.left + i, yy))
                .mapToDouble(input -> horizontal.getNetwork().run(input)[0]);
            boolean isLine = results.allMatch(d -> d > THRESHOLD);
            if (wasLine && !isLine) {
                // begin a square
                horizontalLines.add(y);
            }
            if (!wasLine && isLine) {
                int lineSize = y - horizontalLines.get(horizontalLines.size() - 1);
                if (lineSize < 5) {
                    horizontalLines.remove(horizontalLines.size() - 1);
                } else {
                    // end square
                    horizontalLines.add(y);
                }
            }
            wasLine = isLine;
        }


        List<Integer> verticalLines = new ArrayList<>();
        wasLine = true;
        for (int x = rect.left; x < rect.right; x++) {
            final int xx = x;
            IntStream stream = IntStream.range(0, rect.height() / 4);
            DoubleStream results = stream.<double[]>mapToObj(i -> vertical.imagePart(runImage, xx, rect.top + i))
                .mapToDouble(input -> vertical.getNetwork().run(input)[0]);
            boolean isLine = results.allMatch(d -> d > THRESHOLD);
            if (wasLine && !isLine) {
                // begin a square
                verticalLines.add(x);
            }
            if (!wasLine && isLine) {
                int lineSize = x - verticalLines.get(verticalLines.size() - 1);
                if (lineSize < 5) {
                    verticalLines.remove(verticalLines.size() - 1);
                } else {
                    // end square
                    verticalLines.add(x);
                }
            }
            wasLine = isLine;
        }

        int[] xBoxes = verticalLines.stream().mapToInt(i -> i).toArray();
        int[] yBoxes = horizontalLines.stream().mapToInt(i -> i).toArray();
        ZRect[][] squares = BineroScan.createRectsFromLines(xBoxes, yBoxes);
        return squares;


/*
        List<Integer> verticalLines = new ArrayList<>();
        for (int x = rect.left; x + vertical.getWidth() < rect.right; x++) {
            double[] input = vertical.imagePart(runImage, x, rect.top + 10);
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
        return gridLocations;*/
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
            MyImageUtil.save(networkResult[i].getImage(), "network-result-" + i);
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
