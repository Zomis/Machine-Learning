package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;

public class ImageAnalysis {

    private final int width;
    private final int height;
    private final boolean useGrayscale;

    public ImageAnalysis(int width, int height, boolean useGrayscale) {
        this.width = width;
        this.height = height;
        this.useGrayscale = useGrayscale;
    }

    public ImageNetworkBuilder neuralNetwork(int... hiddenLayerSizes) {
        return new ImageNetworkBuilder(width * height * partsPerPixel(), hiddenLayerSizes);
    }

    public double[] imagePart(BufferedImage image, int x, int y) {
        return imagePart(image, x, y, width, height, useGrayscale);
    }

    public static double[] imagePart(BufferedImage image, int x, int y, int width, int height, boolean useGrayscale) {
        int partsPerPixel = useGrayscale ? 1 : 3;
        double[] result = new double[width * height * partsPerPixel];
        int i = 0;
        for (int yy = y; yy < y + height; yy++) {
            for (int xx = x; xx < x + width; xx++) {
                double[] rgbGray = getRGB(image, xx, yy);
                if (useGrayscale) {
                    result[i++] = rgbGray[4];
                } else {
                    result[i++] = rgbGray[1];
                    result[i++] = rgbGray[2];
                    result[i++] = rgbGray[3];
                }
            }
        }
        return result;
    }

    private int partsPerPixel() {
        return useGrayscale ? 1 : 3;
    }

    public SlidingWindow slidingWindow(ImageNetwork network, BufferedImage image) {
        return new SlidingWindow(this, network, image);
    }

    /**
     * Returns the color of a pixel
     *
     * @param image The image to get the pixel from
     * @param x X coordinate
     * @param y Y coordinate
     * @return An array of Alpha, Red, Green, Blue, and Gray-scale
     */
    public static double[] getRGB(BufferedImage image, int x, int y) {
        if (x < 0) {
            x = image.getWidth() + x;
        }
        if (y < 0) {
            y = image.getHeight() + y;
        }
        int rgb = image.getRGB(x, y);
        int a = (rgb >> 24) & 0xFF;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        double gray = 0.2989 * r + 0.5870 * g + 0.1140 * b;
        return new double[] { a / 255d, r / 255d, g / 255d, b / 255d, gray / 255d };
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
