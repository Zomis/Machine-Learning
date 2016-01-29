package net.zomis.machlearn.clustering;

public class KMeansResult {

    private final int[] clusters;
    private final double[][] centroids;

    public KMeansResult(int[] clusters, double[][] centroids) {
        this.clusters = clusters;
        this.centroids = centroids;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public int[] getClusters() {
        return clusters;
    }

}
