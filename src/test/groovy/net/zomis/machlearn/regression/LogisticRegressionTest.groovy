package net.zomis.machlearn.regression

import net.zomis.machlearn.test.TestHelp
import org.junit.Test

class LogisticRegressionTest {

    double[][] xs = [[1] as double[], [2] as double[], [3] as double[], [4] as double[],
                     [5] as double[], [6] as double[], [7] as double[], [8] as double[],
                     [15] as double[]] as double[][]
    double[] y = [0, 0, 0, 0, 1, 1, 1, 1, 1] as double[]

    static double[] gradientDescent(double[][] xs, double[] y, int thetaParameters) {
        GradientDescent.gradientDescent(LogisticRegression.costFunction(xs, y),
                new ConvergenceIterations(10000), new double[thetaParameters], 0.01)
    }

    @Test(expected = IllegalArgumentException)
    void requiresParameters() {
        gradientDescent(xs, y, 0)
        assert false
    }

    @Test(expected = IllegalArgumentException)
    void requiresMatchingParamSize() {
        gradientDescent(xs, y, 1)
        assert false
    }

    @Test
    void simpleMoreThan5() {
        double[] theta = gradientDescent(xs, y, 2)
        for (int i = -25; i <= 25; i++) {
            boolean expected = i >= 4.5
            double hypothesis = LogisticRegression.hypothesis(theta, [i] as double[])
            boolean hypothesisBoolean = hypothesis >= 0.5
            assert expected == hypothesisBoolean : "$i: $expected vs $hypothesis = $hypothesisBoolean"
        }
    }

}
