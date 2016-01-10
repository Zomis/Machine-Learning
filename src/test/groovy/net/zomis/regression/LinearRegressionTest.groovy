package net.zomis.regression

import net.zomis.machlearn.regression.GradientDescent
import net.zomis.machlearn.regression.LinearRegression
import org.junit.Test

class LinearRegressionTest {

    static double[] gradientDescent(double[][] xs, double[] y, int thetaParameters) {
        GradientDescent.gradientDescent(LinearRegression.costFunction(xs, y),
                new ConvergenceIterations(10000), new double[thetaParameters], 0.01)
    }

    @Test(expected = IllegalArgumentException)
    void requiresParameters() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        gradientDescent(xs, y, 0)
        assert false
    }

    @Test(expected = IllegalArgumentException)
    void requiresMatchingParamSize() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        gradientDescent(xs, y, 1)
        assert false
    }

    @Test
    void simple1_9Line() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        double[] theta = gradientDescent(xs, y, 2)
        assert doubleEquals(theta, [0, 1.9] as double[])
    }

    static boolean doubleEquals(double[] a, double[] b) {
        assert a.length == b.length
        for (int i = 0; i < a.length; i++) {
            double diff = Math.abs(a[i] - b[i])
            if (diff >= 0.000001) {
                return false
            }
        }
        return true
    }

}
