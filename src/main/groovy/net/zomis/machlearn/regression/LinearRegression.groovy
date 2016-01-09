package net.zomis.machlearn.regression

class LinearRegression {

    public static double linearHypothesis(double[] theta, double[] x) {
        if (x.length != theta.length - 1) {
            throw new IllegalArgumentException("Number of features " + Arrays.toString(x) +
                    " does not match the number of theta parameters " + Arrays.toString(theta));
        }
        double sum = theta[0];
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * theta[i + 1];
        }
        return sum;
    }

    public static ModelFunction costFunction(double[][] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException();
        }
        return new ModelFunction() {
            @Override
            public double apply(double[] theta) {
                double sum = 0;
                for (int i = 0; i < x.length; i++) {
                    double expected = y[i];
                    double actual = linearHypothesis(theta, x[i]);
                    double diff = actual - expected;
                    sum += diff * diff;
                }
                return 1d / (2d * x.length) * sum;
            }
        };
    }

}
