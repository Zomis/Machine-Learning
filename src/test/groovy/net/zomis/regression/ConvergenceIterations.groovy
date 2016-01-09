package net.zomis.regression

import java.util.function.Predicate

class ConvergenceIterations implements Predicate<double[]> {

    private int count

    ConvergenceIterations(int count) {
        this.count = count
    }

    @Override
    boolean test(double[] doubles) {
        return count-- == 0
    }
}
