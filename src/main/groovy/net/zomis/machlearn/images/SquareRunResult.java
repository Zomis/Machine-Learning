package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class SquareRunResult {

    private final Map<Object, Double> result;
    private final BufferedImage bestImage;
    private final ZRect bestRect;
    private final Map.Entry<Object, Double> bestEntry;

    public SquareRunResult(Map<Object, Double> result, BufferedImage bestImage, ZRect bestRect) {
        this.result = result;
        this.bestImage = bestImage;
        this.bestRect = bestRect;
        this.bestEntry = result.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue)).orElse(null);
    }

    public Map<Object, Double> getResult() {
        return result;
    }

    public Map.Entry<Object, Double> getBestEntry() {
        return bestEntry;
    }

    public double getBestScore() {
        return bestEntry.getValue();
    }

    public ZRect getBestRect() {
        return bestRect;
    }

    public BufferedImage getBestImage() {
        return bestImage;
    }

}
