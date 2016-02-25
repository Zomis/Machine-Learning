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
    
    public static DoubleMatrix gradientDescent(DoubleMatrix X,DoubleMatrix Y, Predicate<double[]> convergenceCondition,
            double[] initialTheta, double alpha) {
    	DoubleMatrix Xold = X;
    	X = DoubleMatrix.concatHorizontally(DoubleMatrix.ones(X.rows,1),X);
    	//System.out.println("X="+X);
    	DoubleMatrix theta = new DoubleMatrix(initialTheta);
    	//System.out.println("THETA="+theta);
    	//TODO: convergence test doesn't use initialTheta. Should take into account the current Theta value I guess.
		while (!convergenceCondition.test(initialTheta)) {
			//System.out.println("I="+theta);
			//System.out.println("h="+LogisticRegression.hypothesis(theta,X));
			//System.out.println("h-1="+LogisticRegression.hypothesis(theta,X).sub(Y));
			// [0.5;0.5]
			//System.out.println("D="+X.transpose().mmul(LogisticRegression.hypothesis(theta,X).sub(Y)));
			DoubleMatrix derivative = X.transpose().mmul(LogisticRegression.hypothesis(theta,X).sub(Y));
			//System.out.print("der="+derivative);
			//System.out.println("cost="+LogisticRegression.costFunction(Xold.toArray2(), Y.toArray()).apply(theta.toArray()));
			//System.out.println("derxalpha="+derivative.mul(alpha));
			//System.out.println("Theta="+theta);
			//System.out.println("theta-derxalpha="+theta.sub(derivative.mul(alpha)));
			DoubleMatrix thetanew = theta.sub(derivative.mul(alpha));
			theta = thetanew;
			//System.out.println("t-ader-after="+theta);
		}
		return theta;
    }

    public static double[] gradientDescentOld(ModelFunction costFunction, Predicate<double[]> convergenceCondition,
                                           double[] initialTheta, double alpha) {

        double[] theta = Arrays.copyOf(initialTheta, initialTheta.length);
        double[] newTheta = new double[theta.length];

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
