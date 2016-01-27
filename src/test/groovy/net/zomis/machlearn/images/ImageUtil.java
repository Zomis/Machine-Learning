package net.zomis.machlearn.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageUtil {

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
}
