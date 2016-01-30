package net.zomis.machlearn.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MyImageUtil {

    private static final File SAVE_DIRECTORY = new File("visualize");

    public static BufferedImage screenshot() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            return new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage resource(String fileName) {
        URL url = Screenshoter.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new NullPointerException(fileName + " not found");
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(BufferedImage img, String fileName) {
        SAVE_DIRECTORY.mkdirs();
        try {
            ImageIO.write(img, "PNG", new File(SAVE_DIRECTORY, fileName + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage grayscale(BufferedImage image) {
        ImagePainter painter = new ImagePainter(image.getWidth(), image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double[] pixel = ImageAnalysis.getRGB(image, x, y);
                double gray = pixel[4];
                painter.drawGrayscale(x, y, gray);
            }
        }
        return painter.getImage();
    }

}
