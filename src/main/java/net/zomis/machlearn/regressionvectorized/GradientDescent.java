package net.zomis.machlearn.regressionvectorized;

import java.util.Arrays;
import java.util.function.Predicate;

import org.jblas.DoubleMatrix;

public class GradientDescent {

    public static double[] partialDerivatives(ModelFunction function, double[] x) {
        if (x == null || x.length == 0) {
            throw new IllegalArgumentException("Cannot calculate derivative without parameters");
        }
        final double H = 0.0000001;
        double[] result = new double[x.length];
        double[] x2 = Arrays.copyOf(x, x.length);
        for (int i = 0; i < x.length; i++) {
            double fx = function.apply(x);
            x2[i] += H;
            double fxh = function.apply(x2);
            x2[i] = x[i];
            result[i] = (fxh - fx) / H;
        }
        return result;
    }

    public static double[] gradientDescent(ModelFunction costFunction, Predicate<double[]> convergenceCondition,
                                           double[] initialTheta, double alpha) {

        double[] theta = Arrays.copyOf(initialTheta, initialTheta.length);
        double[] newTheta = new double[theta.length];
        DoubleMatrix vector = new DoubleMatrix(new double[]{3, 3, 3, 3,3});
        while (!convergenceCondition.test(theta)) {
            double[] derivate = partialDerivatives(costFunction, theta);
            for (int i = 0; i < theta.length; i++) {
                newTheta[i] = theta[i] - alpha * derivate[i];
            }
            double[] temp = theta;
            theta = newTheta;
            newTheta = temp;
        }
        return theta;
    }

}
