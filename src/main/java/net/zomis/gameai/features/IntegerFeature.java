package net.zomis.gameai.features;

import net.zomis.gameai.Feature;
import net.zomis.gameai.FeatureExtractor;
import net.zomis.gameai.FeatureFunction;

import java.util.function.ToIntFunction;

public class IntegerFeature<E> implements FeatureExtractor<E> {

    private final ToIntFunction<E> valueRetriever;
    private final int bits;
    private final boolean includeFull;
    private final String name;

    public IntegerFeature(String name, ToIntFunction<E> function, int bits, boolean includeFull) {
        this.name = name;
        this.valueRetriever = function;
        this.bits = bits;
        this.includeFull = includeFull;
    }

    @Override
    public Feature<?> extract(E object) {
        int value = valueRetriever.applyAsInt(object);
        FeatureFunction<Integer> ff = (int index, Integer data) -> {
            if (includeFull && index == 0) {
                return data;
            }
            int minus = includeFull ? 1 : 0;
            int shiftLeft = index - minus;
            int shiftedLeft = 1 << shiftLeft;
            int v = data & shiftedLeft;
            return v >> shiftLeft;
        };
        return new Feature<>(bits, name, value, ff);
    }

}
