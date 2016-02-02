package net.zomis.machlearn.images.minesweeper;

import net.zomis.machlearn.images.MinesweeperScan;
import net.zomis.machlearn.images.ZPoint;
import net.zomis.machlearn.images.ZRect;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

/**
 * A class for automatically playing Minesweeper. So far only tested with the Minesweeper in Windows 8.1
 */
public class MinesweeperBot {

    private final Supplier<BufferedImage> imageSupplier;
    private final MinesweeperScan minesweeperScan;
    private ZRect[][] positions;
    private ZRect boardArea;

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
        initialize(image);


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

    private void initialize(BufferedImage image) {
        if (boardArea == null) {
            boardArea = MinesweeperScan.findEdges(minesweeperScan.edgeFind, image);
            boardArea.expand(20);
            this.positions = minesweeperScan.findGrid(image, boardArea);

            initializeClustersAndCentroids(20);
            combineClusters(50d);
        }
    }

    private void combineClusters(double maxDistance) {

    }

    private void initializeClustersAndCentroids(int clusterCount) {

    }

}
