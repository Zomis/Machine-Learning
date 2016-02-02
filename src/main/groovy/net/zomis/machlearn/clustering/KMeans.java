package net.zomis.machlearn.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMeans {

    public static void main(String[] args) {
        Random random = new Random(42);
        double[][] inputs = new double[12][2];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = new double[] { random.nextDouble(), random.nextDouble() };
        }
        System.out.println("a = [");
        Arrays.stream(inputs).forEach(d -> System.out.println(Arrays.toString(d) + ";"));
        System.out.println(']');
        int[] clusters = cluster(inputs, 2, 100, random);
        System.out.println("clusters = " + Arrays.toString(clusters) + ';');
        System.out.println("a(:,4) = clusters'");
    }

    public static int[] cluster(double[][] inputs, int clusterCount, int repetitions, Random random) {
        // PERFORM FEATURE-SCALING ON INPUTS

        int[] bestClusters = null;
        double bestCost = 0;
        for (int iteration = 0; iteration < repetitions; iteration++) {
            KMeansResult result = performClustering(inputs, clusterCount, random);
            int[] clusters = result.getClusters();
            double[][] centroids = result.getCentroids();

            double totalCost = 0;
            for (int i = 0; i < inputs.length; i++) {
                int cluster = clusters[i];
                double[] centroid = centroids[cluster];
                double distance = eucledianDistanceSquared(inputs[i], centroid);
                totalCost += distance;
            }
            if (bestClusters == null || totalCost < bestCost) {
                bestCost = totalCost;
                bestClusters = clusters;
            }
        }
        return bestClusters;
    }

    private static KMeansResult performClustering(double[][] inputs, int clusterCount, Random random) {
        int[] clusters = new int[inputs.length];
        double[][] centroids = new double[clusterCount][inputs[0].length];
        int[] trainingSetCentroids = new int[centroids.length];
        for (int i = 0; i < centroids.length; i++) {
            // Initialize centroids to random training set, don't initialize to the same trainingSet
            int trainingSet;
            do {
                trainingSet = random.nextInt(inputs.length);
                trainingSetCentroids[i] = trainingSet;
            } while (isTaken(trainingSetCentroids, i, trainingSet));
            centroids[i] = Arrays.copyOf(inputs[trainingSet], inputs[trainingSet].length);
        }

        /* Repeat until convergence:
         * 1. Mark the clusters according to which one is closest
         * 2. Move centroids
         */
        boolean changed = true;
        while (changed) {
            changed = changeClusters(centroids, clusters, inputs);
            moveCentroids(centroids, clusters, inputs);
        }
        return new KMeansResult(clusters, centroids);
    }

    private static void moveCentroids(double[][] centroids, int[] clusters, double[][] inputs) {
        List<List<Integer>> trainingSetsInCluster = new ArrayList<>(centroids.length);
        for (int i = 0; i < centroids.length; i++) {
            trainingSetsInCluster.add(new ArrayList<>());
        }

        for (int i = 0; i < inputs.length; i++) {
            int cluster = clusters[i];
            trainingSetsInCluster.get(cluster).add(i);
        }

        for (int c = 0; c < trainingSetsInCluster.size(); c++) {
            double[] sums = new double[inputs[0].length];
            List<Integer> trainingSets = trainingSetsInCluster.get(c);
            for (int i : trainingSets) {
                for (int j = 0; j < inputs[i].length; j++) {
                    sums[j] += inputs[i][j];
                }
            }
            centroids[c] = Arrays.stream(sums).map(d -> d / trainingSets.size()).toArray();
        }
    }

    private static boolean changeClusters(double[][] centroids, int[] clusters, double[][] inputs) {
        boolean changed = false;
        for (int i = 0; i < inputs.length; i++) {
            int oldCluster = clusters[i];
            clusters[i] = findClosestCluster(inputs[i], centroids);
            changed = changed || (oldCluster != clusters[i]);
        }
        return changed;
    }

    private static int findClosestCluster(double[] input, double[][] centroids) {
        double minDistance = eucledianDistanceSquared(input, centroids[0]);
        int closestIndex = 0;
        for (int i = 1; i < centroids.length; i++) {
            double distance = eucledianDistanceSquared(input, centroids[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    private static double eucledianDistanceSquared(double[] input, double[] centroid) {
        if (input.length != centroid.length) {
            throw new IllegalArgumentException("Values must be of same length. Input has length " + input.length +
                    "while centroid has length " + centroid.length);
        }
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            double diff = input[i] - centroid[i];
            sum += diff * diff;
        }
        return sum;
    }

    private static boolean isTaken(int[] centroids, int upToIndex, int current) {
        for (int i = 0; i < upToIndex; i++) {
            if (centroids[i] == current) {
                return true;
            }
        }
        return false;
    }

}
