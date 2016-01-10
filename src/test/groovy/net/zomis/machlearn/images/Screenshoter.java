package net.zomis.machlearn.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Screenshoter {

    public static void main(String[] args) throws AWTException, IOException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        // BufferedImage capture = new Robot().createScreenCapture(screenRect);

        String fileName = "challenge-flags-16x16.png";
//        String fileName = "different-colors.png";
        URL url = Screenshoter.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new NullPointerException(fileName + " not found");
        }
        BufferedImage image = ImageIO.read(url);
        ImageAnalysis analyze = new ImageAnalysis(39, 39, true);
        ImageNetwork network = analyze.neuralNetwork(40, 20, 10)
            .classify("unclicked", analyze.imagePart(image, 619, 197))
            .classify("clicked", analyze.imagePart(image, 662, 197))
            .classify("clicked", analyze.imagePart(image, 790, 241))
            .classify("flag", analyze.imagePart(image, 790, 197))
            .classifyNone(analyze.imagePart(image, 0, 0))
            .learn();
//        SlidingWindowResult points = analyze.slidingWindow(network, image).scaleX(25, 60).step(4).overlapping(false).run();
        network.getNetwork().printAll();

        test(network, analyze, image, "clicked 2", 834, 284);
        test(network, analyze, image, "clicked 2", 1178, 370);
        test(network, analyze, image, "clicked 4", 1092, 327);
        test(network, analyze, image, "flag", 1049, 325);
        test(network, analyze, image, "middle junk", 1000, 500);
        test(network, "zero", new double[39*39]);

        System.out.println("TOP-LEFT  " + Arrays.toString(ImageAnalysis.getRGB(image, 2, 2)));
        System.out.println("TOP-RIGHT " + Arrays.toString(ImageAnalysis.getRGB(image, -2, 2)));
        System.out.println("BTM-LEFT  " + Arrays.toString(ImageAnalysis.getRGB(image, 2, -2)));
        System.out.println("BTM-RIGHT " + Arrays.toString(ImageAnalysis.getRGB(image, -2, -2)));
    }

    private static void test(ImageNetwork network, ImageAnalysis analyze, BufferedImage image, String text, int x, int y) {
        double[] values = analyze.imagePart(image, x, y);
        test(network, text, values);
    }

    private static void test(ImageNetwork network, String text, double[] values) {
        System.out.printf("%10s: %s values %s", text, network.run(values), Arrays.toString(values));
    }

}
