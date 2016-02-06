package net.zomis.gameai;

public class Feature<T> {

    // Example: Game21.state % 4
    // Example: Game21.state (int)
    // Example: Game21.currentPlayer
    // Example: Game21.steps

    private final int size;
    private final String name;
    private final T value;
    private final FeatureFunction<T> featureFunction;

    public Feature(int size, String name, T value, FeatureFunction<T> featureFunction) {
        this.size = size;
        this.name = name;
        this.value = value;
        this.featureFunction = featureFunction;
    }

    public double toDouble(int index) {
        return featureFunction.value(index, value);
    }

    public int getSize() {
        return size;
    }

}
