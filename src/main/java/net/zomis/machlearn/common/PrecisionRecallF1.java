package net.zomis.machlearn.common;

public class PrecisionRecallF1 {

    private int[][] values = new int[2][2];

    public void add(boolean actual, boolean predicted) {
        int predictedClass = predicted ? 1 : 0;
        int actualClass = actual ? 1 : 0;
        values[predictedClass][actualClass]++;
    }

    public int get(boolean actual, boolean predicted) {
        int predictedClass = predicted ? 1 : 0;
        int actualClass = actual ? 1 : 0;
        return values[predictedClass][actualClass];
    }

    public void falsePositive() {
        add(false, true);
    }

    public void falseNegative() {
        add(true, false);
    }

    public void truePositive() {
        add(true, true);
    }

    public void trueNegative() {
        add(false, false);
    }

    public double getPrecision() {
        return (double) getTruePositive() / (getTruePositive() + getFalsePositive());
    }

    public double getRecall() {
        return (double) getTruePositive() / (getTruePositive() + getFalseNegative());
    }

    public int getTruePositive() {
        return get(true, true);
    }

    public int getTrueNegative() {
        return get(false, false);
    }

    public int getFalsePositive() {
        return get(false, true);
    }

    public int getFalseNegative() {
        return get(true, false);
    }

    public double getF1Score() {
        double precision = getPrecision();
        double recall = getRecall();
        return (2 * precision * recall) / (precision + recall);
    }

    @Override
    public String toString() {
        return String.format("truePositive: %d, falsePositive: %d," +
                " trueNegative: %d, falseNegative: %d," +
                " precision %f, recall %f, f1 score: %f",
                getTruePositive(), getFalsePositive(), getTrueNegative(), getFalseNegative(),
                getPrecision(), getRecall(), getF1Score());
    }

}
