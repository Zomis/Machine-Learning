package net.zomis.gameai;

public interface FeatureFunction<T> {

    double value(int index, T data);

}
