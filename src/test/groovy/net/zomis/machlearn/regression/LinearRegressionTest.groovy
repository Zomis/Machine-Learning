package net.zomis.machlearn.regression

import net.zomis.machlearn.test.TestHelp
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
        assert TestHelp.doubleEquals(theta, [0, 1.9] as double[])
    }

}
