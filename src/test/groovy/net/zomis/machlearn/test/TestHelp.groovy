package net.zomis.machlearn.test

class TestHelp {

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
