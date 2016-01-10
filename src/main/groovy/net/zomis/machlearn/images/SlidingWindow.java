package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class SlidingWindow {

    private final ImageNetwork network;
    private final BufferedImage image;
    private int minScaleX;
    private int maxScaleX;
    private int step;
    private boolean overlapping;

    public SlidingWindow(ImageNetwork network, BufferedImage image) {
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
        return null;
    }

    public SlidingWindow overlapping(boolean b) {
        this.overlapping = b;
        return this;
    }
}
