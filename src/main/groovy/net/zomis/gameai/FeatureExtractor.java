package net.zomis.gameai;

public interface FeatureExtractor<T> {

    Feature<?> extract(T object);

}
