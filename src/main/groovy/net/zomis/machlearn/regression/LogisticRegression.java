package net.zomis.machlearn.regression;

class LogisticRegression {

    public static double sigmoid(double z) {
        double n = (1 + Math.exp(-z));
        return 1 / n;
    }

    public static double hypothesis(double[] theta, double[] x) {
        double thetaX = LinearRegression.linearHypothesis(theta, x);
        return sigmoid(thetaX);
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
