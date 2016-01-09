package net.zomis.regression

import net.zomis.machlearn.regression.GradientDescent
import net.zomis.machlearn.regression.LinearRegression
import org.junit.Test

class LinearRegressionTest {

    @Test(expected = IllegalArgumentException)
    void requiresParameters() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        GradientDescent.gradientDescent(LinearRegression.costFunction(xs, y),
                new ConvergenceIterations(10000), new double[0], 0.01)
        assert false
    }

    @Test(expected = IllegalArgumentException)
    void requiresMatchingParamSize() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        GradientDescent.gradientDescent(LinearRegression.costFunction(xs, y),
                new ConvergenceIterations(10000), new double[1], 0.01)
        assert false
    }

    @Test
    void simple1_9Line() {
        double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[]] as double[][]
        double[] y = [2, 4, 5, 8] as double[]
        double[] theta = GradientDescent.gradientDescent(LinearRegression.costFunction(xs, y),
            new ConvergenceIterations(10000), new double[2], 0.01)
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
