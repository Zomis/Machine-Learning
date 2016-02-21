package net.zomis.machlearn.images;

import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.util.*;

public class SlidingWindow {

    private final ImageNetwork network;
    private final BufferedImage image;
    private final ImageAnalysis analysis;
    private int minScaleX;
    private int maxScaleX;
    private int step;
    private boolean overlapping;

    public SlidingWindow(ImageAnalysis analysis, ImageNetwork network, BufferedImage image) {
        if (analysis.getWidth() != analysis.getHeight()) {
            throw new IllegalArgumentException("Different width and height is not yet supported");
        }
        this.analysis = analysis;
        this.network = network;
        this.image = image;
    }

    public SlidingWindow scaleX(int min, int max) {
        this.minScaleX = min;
        this.maxScaleX = max;
        return this;
    }

    public SlidingWindow step(int stepSize) {
        this.step = stepSize;
        return this;
    }

    public SlidingWindowResult run() {
        Map<ZPoint, Set<Object>> foundObjects = new HashMap<>();

        for (int size = minScaleX; size < maxScaleX; size += 2) {
            runWithSize(size, foundObjects);
        }

        return new SlidingWindowResult(foundObjects);
    }

    private void runWithSize(int size, Map<ZPoint, Set<Object>> foundObjects) {
        System.out.println("Running with size " + size + " found so far " + foundObjects.size());
        int bottom = image.getHeight() - size;
        int right = image.getWidth() - size;
        for (int y = 0; y < bottom + step; y += step) {
            if (y > bottom) {
                y = bottom;
            }
            System.out.println("Running y " + y + " found so far " + foundObjects);
            for (int x = 0; x < right + step; x++) {
                if (x > right) {
                    x = right;
                }

                if (!overlapping) {
                    final int xv = x;
                    final int yv = y;
                    boolean overlaps = foundObjects.keySet().stream()
                        .anyMatch(point -> point.isInside(xv, yv, xv + size, yv + size));
                    if (overlaps) {
                        continue;
                    }
                }

                Set<Object> results = runAt(image, size, size, x, y);
                if (results != null) {
                    foundObjects.put(new ZPoint(x, y), results);
                }
                if (x == right) {
                    break;
                }
            }
            if (y == bottom) {
                break;
            }
        }
    }

    private Set<Object> runAt(BufferedImage image, int sizeX, int sizeY, int x, int y) {
        double[] input = convertImageToInput(image, sizeX, sizeY, x, y);

        double[] networkResults = network.getNetwork().run(input);
        Set<Object> results = null;
        for (int i = 0; i < networkResults.length; i++) {
            if (networkResults[i] >= 0.75) {
                if (results == null) {
                    results = new HashSet<>();
                }
                results.add(network.getObject(i));
            }
        }
        if (results != null) {
            System.out.printf("Found %s at %d, %d with values %s%n",
                results, x, y, Arrays.toString(networkResults));
        }
        return results;
    }

    private double[] convertImageToInput(BufferedImage image, int sizeX, int sizeY, int x, int y) {
        BufferedImage cropped = Scalr.crop(image, x, y, sizeX, sizeY);
        BufferedImage input = Scalr.resize(cropped, analysis.getWidth(), analysis.getHeight());
        return ImageAnalysis.imagePart(input, 0, 0, analysis.getWidth(), analysis.getHeight(), true);
    }

    public SlidingWindow overlapping(boolean b) {
        this.overlapping = b;
        return this;
    }
}
