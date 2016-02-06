package net.zomis.gameai;

import java.util.Arrays;

public class TrainingData {

    private double[] x;
    private double[] y;

    public TrainingData(double[] x) {
        this.x = x;
    }

    public void setY(double[] y) {
        this.y = y;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public void expandX(double[] data) {
        double[] oldX = x;
        x = Arrays.copyOf(oldX, x.length + data.length);
//      TODO: Use System.arraycopy()
        for (int i = oldX.length; i < x.length; i++) {
            x[i] = data[i - oldX.length];
        }
    }

}
