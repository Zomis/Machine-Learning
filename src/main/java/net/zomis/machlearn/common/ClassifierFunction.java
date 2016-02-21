package net.zomis.machlearn.common;

public interface ClassifierFunction {

    boolean classify(double[] theta, double[] x);

}
