package net.zomis.machlearn.images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class ImagePainter {
    private final BufferedImage img;
    private final Graphics2D graphics;

    public ImagePainter(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, width, width);
    }

    public ImagePainter(BufferedImage image) {
        img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.drawImage(image, 0, 0, null);
    }

    public void drawGrayscale(int x, int y, double value) {
        int grayscaleValue = (int) (value * 255);
        int rgb = 0xff << 24 | grayscaleValue << 16 | grayscaleValue << 8 | grayscaleValue;
        img.setRGB(x, y, rgb);
    }

    public BufferedImage getImage() {
        return img;
    }

    public void drawGrayscale(ZRect rect, double value) {
        int grayscaleValue = (int) (value * 255);
        Color color = new Color(grayscaleValue, grayscaleValue, grayscaleValue);
        graphics.setColor(color);
        graphics.fillRect(rect.left, rect.top, rect.width(), rect.height());
    }

    public void save(String file) {
        MyImageUtil.save(img, file);
    }

    public void drawRGB(ZRect rect, double r, double g, double b) {
        int ri = (int) (r * 255);
        int gi = (int) (g * 255);
        int bi = (int) (b * 255);
        Color color = new Color(ri, gi, bi);
        graphics.setColor(color);
        graphics.fillRect(rect.left, rect.top, rect.width(), rect.height());
    }

    public static ImagePainter visualizeNetwork(ImageNetwork network,
            int rightBorder, int bottomBorder,
            BufferedImage image, XYToDoubleArray inputFunction,
            ToDoubleFunction<double[]> outputFunction) {
        ImagePainter painter = new ImagePainter(image.getWidth(), image.getHeight());

        int maxX = image.getWidth() - rightBorder;
        int maxY = image.getHeight() - bottomBorder;

        for (int y = 0; y < maxY; y++) {
            if (y % 40 == 0) {
                System.out.println("process y " + y);
            }
            for (int x = 0; x < maxX; x++) {
                double[] input = inputFunction.toInput(network, image, x, y);
                double[] output = network.getNetwork().run(input);
                double value = outputFunction.applyAsDouble(output);
                painter.drawGrayscale(x, y, value);
            }
        }
        return painter;
    }

    public static ImagePainter[] visualizeNetworks(ImageNetwork network,
            int rightBorder, int bottomBorder,
            BufferedImage image, XYToDoubleArray inputFunction) {
        ImagePainter[] painters = new ImagePainter[network.getOutputs().length];
        for (int i = 0; i < painters.length; i++) {
            ImagePainter painter = new ImagePainter(image.getWidth(), image.getHeight());
            painters[i] = painter;
        }

        int maxX = image.getWidth() - rightBorder;
        int maxY = image.getHeight() - bottomBorder;

        for (int y = 0; y < maxY; y++) {
            if (y % 40 == 0) {
                System.out.println("process y " + y);
            }
            for (int x = 0; x < maxX; x++) {
                double[] input = inputFunction.toInput(network, image, x, y);
                double[] output = network.getNetwork().run(input);
                for (int i = 0; i < output.length; i++) {
                    painters[i].drawGrayscale(x, y, output[i]);
                }
            }
        }
        return painters;
    }

    public void drawGrids(ZRect[][] gridLocations) {
        Color[] colors = { new Color(1, 0, 0, 0.6f), new Color(0, 1, 0, 0.6f), new Color(0, 0, 1, 0.6f),
                new Color(1, 1, 0, 0.6f), new Color(1, 0, 1, 0.6f), new Color(0, 1, 1, 0.6f)};
        for (int y = 0; y < gridLocations.length; y++) {
            for (int x = 0; x < gridLocations[y].length; x++) {
                ZRect rect = gridLocations[y][x];
                if (rect == null) {
                    continue;
                }
                Color color = colors[(y * 2 + x) % colors.length];
                this.drawRect(rect, color);
            }
        }
    }

    public void drawRect(ZRect rect, Color color) {
        graphics.setColor(color);
        graphics.fillRect(rect.left, rect.top, rect.width(), rect.height());
    }
}
