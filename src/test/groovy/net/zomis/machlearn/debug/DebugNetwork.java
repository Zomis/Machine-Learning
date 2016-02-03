package net.zomis.machlearn.debug;

import groovy.lang.GroovyShell;
import net.zomis.machlearn.images.ImageAnalysis;
import net.zomis.machlearn.images.ImageNetwork;
import net.zomis.machlearn.images.MinesweeperTrainingBoard;
import net.zomis.machlearn.images.MyImageUtil;
import net.zomis.machlearn.neural.Backpropagation;

import java.awt.image.BufferedImage;
import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class DebugNetwork {

    private static String LEARN_IMAGE = "challenge-flags-16x16.png";
    private static BufferedImage img = MyImageUtil.resource(LEARN_IMAGE);

    public static void main(String[] args) {
        ImageNetwork network = network();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
//            GroovyShell sh = new GroovyShell();
//            sh.evaluate(input);
            // 817, 959, 963, 1106, 1111, 1153
//            861, 910
            for (int i = 700; i < 1200; i++) {
                DoubleSummaryStatistics sum = runWith(network, i);
                System.out.println(i + ": " + sum);
            }
// [325, 365, 375, 414, 423, 464, 472, 513, 521, 562, 570, 611, 620, 661, 663, 664, 665, 667, 669, 710, 717, 759, 767, 809, 817, 959, 963, 1106, 1111, 1153, 1161, 1202, 1212, 1249, 1259, 1300, 1311, 1348, 1358, 1399, 1407, 1448, 1456, 1497, 1506, 1546, 1556, 1595]

//            runWith(network, 861);
//            runWith(network, 910);
        }
    }

    private static DoubleSummaryStatistics runWith(ImageNetwork network, int xx) {
        // {left=324, top=192, right=1595, bottom=903}
        // {left=304, top=172, right=1615, bottom=923}
        IntStream stream = IntStream.range(0, 187);
        MinesweeperTrainingBoard board = MinesweeperTrainingBoard.fromResource("challenge-press-26x14");
        DoubleStream results = stream.<double[]>mapToObj(i -> network.imagePart(board.getImage(), xx, i))
                .mapToDouble(input -> network.getNetwork().run(input)[0]);
        return results.summaryStatistics();
//        System.out.println(results.summaryStatistics());
    }

    private static ImageNetwork network() {
        ImageAnalysis verticalAnalysis = new ImageAnalysis(1, 50, true);
        Backpropagation fastBackprop = new Backpropagation(0.1, 10000);
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
                .learn(fastBackprop, new Random(42));

        return vertical;
    }

}
