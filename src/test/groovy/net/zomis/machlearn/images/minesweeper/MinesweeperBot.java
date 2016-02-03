package net.zomis.machlearn.images.minesweeper;

import net.zomis.machlearn.clustering.KMeans;
import net.zomis.machlearn.clustering.KMeansResult;
import net.zomis.machlearn.images.ImageAnalysis;
import net.zomis.machlearn.images.MinesweeperScan;
import net.zomis.machlearn.images.ZPoint;
import net.zomis.machlearn.images.ZRect;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Supplier;

/**
 * A class for automatically playing Minesweeper. So far only tested with the Minesweeper in Windows 8.1
 */
public class MinesweeperBot {

    private final Supplier<BufferedImage> imageSupplier;
    private final MinesweeperScan minesweeperScan;
    private MinesweeperSquare[][] positions;
    private ZRect boardArea;
    private ImageAnalysis imageAnalysis;
    private List<MinesweeperValueCluster> clusters;

    static class MinesweeperValueCluster {
        double[] centroid;
        Set<MinesweeperSquare> rects = new HashSet<>();
        ValueProbabilities probabilities;
    }

    static class ValueProbabilities {
        double[] numbers;
        double mine;
        double unclicked;
        double blocked;
    }

    static class MinesweeperSquare {
        ZRect position;
        int x, y;
        double[] imageData;
    }

    public MinesweeperBot(Supplier<BufferedImage> imageSupplier) {
        this.imageSupplier = imageSupplier;
        this.minesweeperScan = new MinesweeperScan();
    }

    public static void main(String[] args) {
        Supplier<BufferedImage> imageSupplier = new ResourceSupplier("9x9-unclicked", "9x9-2", "9x9-3", "9x9-4");
        MinesweeperBot bot = new MinesweeperBot(imageSupplier);
        bot.play();
    }

    private ZPoint play() {
        BufferedImage image = imageSupplier.get();
        if (!initialize(image)) {
//            reAnalyze(image);
        }

//        int[][] clusters = analyze(image);
        findPossibleBoards();

        /*
        * Initialize:
        * - Create a bunch of clusters and centroids
        * - Combine clusters if they are close together
        *
        * After each move:
        * - Re-assign a cluster for all fields
        *  - If the closest cluster has a distance of > x, create a new cluster with this as a centroid
        *  - If any cluster was created or changed, re-assign clusters again
        *
        * ? If detecting abnormalities, for each cluster: Loop through the values and split the cluster
        *     (0 and 1 can be within the same cluster for example). Create a new cluster at square XY and re-assign
        *     then possibly re-combine clusters again.
        *
        *  Now with a map like the below:
 0  0  0  0  0  0  0  0  0
 0  0  0  0  0  0  0  0  0
 0  0  0  0  0  0  0  0  0
 0 19  2  2  2  2  0  0  0
 0  2  1  1  1  2  0  0  0
 0  2  2  1  1  2  0  0  0
 0  0 19  2  2  2  0  0  0
 0  0  0  0  0  0  0  0  0
 0  0  0  0  0  0  0  0  0
        * - Detect the middle, what values are there? If those only exist in middle, then they might be blocked.
        * - Find the most popular value. It's probably either open field, 1's, flags, or unclicked.
        * - If you assume it is open field: Check the unique neighbors of open field.
        *    If there are too many, then it's not an open field.
        * - An open field does not create, or is very unlikely to create islands, such as this:
        * 0 0 0
        * 0 ? 0 <--- the only possible value for ? is 0 or unclicked
        * 0 0 0
        * - For each set of valid values, determine how many solutions are available in that
        *
        * - If you click and more than one field is affected, then it was an open field
        */

        return null;
    }

    private void findPossibleBoards() {
        MinesweeperValueCluster popularCluster = clusters.stream().max(Comparator.comparingInt(cl -> cl.rects.size())).get();
//        findPossibleValues(popularCluster);
        Map<MinesweeperValueCluster, Character> assignments = new HashMap<>();
        assignments.put(popularCluster, '8');
//        double possibilities = test(assignments);


    }

    private boolean initialize(BufferedImage image) {
        if (boardArea == null) {
            boardArea = MinesweeperScan.findEdges(minesweeperScan.edgeFind, image);
            boardArea.expand(20);
            ZRect[][] gridRects = minesweeperScan.findGrid(image, boardArea);
            positions = new MinesweeperSquare[gridRects.length][gridRects[0].length];
            for (int y = 0; y < gridRects.length; y++) {
                for (int x = 0; x < gridRects[y].length; x++) {
                    MinesweeperSquare sq = new MinesweeperSquare();
                    positions[y][x] = sq;
                    sq.x = x;
                    sq.y = y;
                    sq.position = gridRects[y][x];
                    sq.imageData = imageAnalysis.imagePart(image, x, y);
                }
            }

            initializeClustersAndCentroids(20);
            combineClusters(50d);
            return true;
        }
        return false;
    }

    private void initializeClustersAndCentroids(int clusterCount) {
        if (this.clusters == null) {
            this.clusters = new ArrayList<>(clusterCount);
        }
        while (this.clusters.size() < clusterCount) {
            this.clusters.add(new MinesweeperValueCluster());
        }
        KMeansResult result = KMeans.cluster(getInputs(), clusterCount, 100, new Random());
        double[][] centroids = result.getCentroids();
        for (int i = 0; i < clusters.size(); i++) {
            MinesweeperValueCluster cluster = clusters.get(i);
            cluster.centroid = centroids[i];
        }

        int[] clusters = result.getClusters();
        i2xy(clusters, getWidth(), (x, y, i) -> this.clusters.get(i).rects.add(this.positions[y][x]));
    }

    private static void i2xy(int[] array, int width, XYIConsumer consumer) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < array.length; i++) {
            consumer.handle(x, y, i);
            x++;
            if (x == width) {
                x = 0;
                y++;
            }
        }
    }

    private double[][] getInputs() {
        double[][] result = new double[getWidth() * getHeight()][];
        int i = 0;
        for (int y = 0; y < this.positions.length; y++) {
            for (int x = 0; x < this.positions[y].length; x++) {
                result[i] = this.positions[y][x].imageData;
            }
        }
        return result;
    }

    public int getHeight() {
        return this.positions.length;
    }

    public int getWidth() {
        return this.positions[0].length;
    }

    private void combineClusters(double maxDistance) {

    }

}
