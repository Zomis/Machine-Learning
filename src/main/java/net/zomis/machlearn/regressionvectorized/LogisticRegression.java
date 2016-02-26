package net.zomis.machlearn.regressionvectorized;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import net.zomis.machlearn.regressionvectorized.ModelFunction;

public class LogisticRegression {

    public static double sigmoid(double z) {
        double n = (1 + Math.exp(-z));
        return 1 / n;
    }
    
    public static DoubleMatrix sigmoid(DoubleMatrix z) {
    	DoubleMatrix n =  DoubleMatrix.ones(z.rows,z.columns).div(MatrixFunctions.expi(z.neg()).add(1.0));
    	//System.out.println(n);
        return n;
    }

    public static double hypothesis(double[] theta, double[] x) {
        double thetaX = LinearRegression.linearHypothesis(theta, x);
        return sigmoid(thetaX);
    }

    public static DoubleMatrix hypothesis(DoubleMatrix T, DoubleMatrix X) {
        return sigmoid(X.mmul(T));
    }
    

    public static ModelFunction costFunction(double[][] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException();
        }
        return new ModelFunction() {
            @Override
            public double apply(double[] theta) {
            	// Add ones column at the front (for the first theta)
            	DoubleMatrix X = DoubleMatrix.concatHorizontally(DoubleMatrix.ones(x.length,1),new DoubleMatrix(x));
            	DoubleMatrix Y = new DoubleMatrix(y);
            	DoubleMatrix T = new DoubleMatrix(theta);
            	DoubleMatrix H = hypothesis(T,X);
            	int m = x.length;
            	// J = sum(-y' * log(H) - (1 - y')*log(1 - H)) / m;
            	
            	//-y' * log(h)
            	DoubleMatrix result1 = Y.transpose().neg().mul(MatrixFunctions.log(H));
            	
            	// (1 - y')*log(1 - H)
            	DoubleMatrix result2 = DoubleMatrix.ones(Y.columns,Y.rows).sub(Y.transpose()).mul(MatrixFunctions.log(DoubleMatrix.ones(H.rows,H.columns).sub(H)));
            	
            	Double result = result1.sub(result2).sum()/m;
            	return result;
            	
            }
        };
    }
    
    public static ModelFunction costFunctionOld(double[][] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException();
        }
        return new ModelFunction() {
            @Override
            public double apply(double[] theta) {
                double sum = 0;
                for (int i = 0; i < x.length; i++) {
                    double current;
                    double yValue = y[i];
                    double hypValue = hypothesis(theta, x[i]);
                    if (yValue == 1) {
                        current = -Math.log(hypValue);
                    } else if (yValue == 0) {
                        current = -Math.log(1 - hypValue);
                    } else {
                        throw new IllegalArgumentException("y must be either 0 or 1 but was " + yValue);
                    }
                    sum += current;
                }
                return sum / x.length;
            }
        };
    }


}
