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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BineroScan {

    private final ImageNetwork imageToRects;
    private final ImageNetwork rectsToNumber;

    public BineroScan() {
        imageToRects = constructImageToRectsNetwork();
        rectsToNumber = constructRectToNumberNetwork();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BineroScan scan = new BineroScan();
        do {
            BufferedImage image = MyImageUtil.screenshot();
            try {
                scan.run(image, true);
            } catch (RuntimeException ex) {
                System.out.println("Error analyzing board. Board was probably not in the center of screen");
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
                MyImageUtil.save(image, "failure-" + time);
                ex.printStackTrace();
            }
            System.out.println("Go again?");
        } while (scanner.nextLine().equals("y"));
        scanner.close();
    }

    public void run(BufferedImage image, boolean click) {
        ZRect[][] boardRects = imageToRects(imageToRects, imageToRects.flip(), image,
            new ZRect(80, 80, image.getWidth() - 100, image.getHeight() - 100),
            image.getHeight() / 2);
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
                String solution = IntegerPoints.map(analysis.getSolutions().get(0).getSetGroupValues(), size.get());
                System.out.println(solution);
                System.out.println("---");
            }
            System.out.println(analysis.getSolutions());
            System.out.println(analysis.getTotal());
            if (analysis.getTotal() == 1) {
                String solution = IntegerPoints.map(analysis.getSolutions().get(0).getSetGroupValues(), size.get());
                String[] solutionRows = solution.split("\n");
                if (click) {
                    MyRobot robot = new MyRobot();
                    for (int y = 0; y < solutionRows.length; y++) {
                        String solutionRow = solutionRows[y];
                        String boardRow = board.split("\n")[y];
                        for (int x = 0; x < solutionRow.length(); x++) {
                            char original = boardRow.charAt(x);
                            char current = solutionRow.charAt(x);
                            if (original != current) {
                                ZRect rect = boardRects[y][x];
                                int clickX = rect.left + rect.width() / 2;
                                int clickY = rect.top + rect.height() / 2;
                                boolean isOne = current == '1';
                                int clickTimes = isOne ? 2 : 1;
                                for (int i = 0; i < clickTimes; i++) {
                                    robot.clickOn(clickX, clickY);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String valuesForBoard(BufferedImage image, ZRect[][] boardRects) {
        ImageNetwork network = this.rectsToNumber;
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
        BufferedImage learningImage = MyImageUtil.resource("binero/binero.png");
        ImageAnalysis analysis = new ImageAnalysis(30, 30, true);
        Backpropagation backprop = new Backpropagation(0.1, 2000).setLogRate(400);
        return analysis.neuralNetwork(20)
            .classify(0, analysis.imagePart(learningImage, 626, 76))
            .classify(1, analysis.imagePart(learningImage, 458, 76))
            .classifyNone(analysis.imagePart(learningImage, 593, 76))
            .learn(backprop, new Random(42));
    }

    public static ZRect[][] imageToRects(ImageNetwork networkX, ImageNetwork networkY, BufferedImage image,
             ZRect scanArea, int middleY) {
        List<Integer> xSeparatorLines = new ArrayList<>();
        for (int x = scanArea.left; x <= scanArea.right; x++) {
            if (x > image.getWidth() - networkX.getWidth()) {
                break;
            }
            double[] output = networkX.getNetwork().run(networkX.imagePart(image, x, middleY));
            if (output[0] > 0.6) {
                xSeparatorLines.add(x);
            }
        }
        System.out.println(xSeparatorLines);
        int[] xBoxes = createBox(xSeparatorLines);

        int middleX = xBoxes[xBoxes.length / 2];

        List<Integer> ySeparatorLines = new ArrayList<>();
        for (int y = scanArea.top; y <= scanArea.bottom; y++) {
            if (y > image.getHeight() - networkY.getHeight()) {
                break;
            }
            double[] output = networkY.getNetwork().run(ImageAnalysis.imagePart(image, middleX, y,
                    networkY.getWidth(), networkY.getHeight(), true));
            if (output[0] > 0.6) {
                ySeparatorLines.add(y);
            }
        }
        System.out.println(ySeparatorLines);
        int[] yBoxes = createBox(ySeparatorLines);
        return createRectsFromLines(xBoxes, yBoxes);
    }

    public static ZRect[][] createRectsFromLines(int[] xBoxes, int[] yBoxes) {
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

    public static int[] createBox(List<Integer> values) {
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
        BufferedImage image = MyImageUtil.resource("binero/binero.png");
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
