package net.zomis.machlearn.images.minesweeper;

import net.zomis.machlearn.images.MinesweeperTrainingBoard;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.function.Supplier;

public class ResourceSupplier implements Supplier<BufferedImage> {

    private final String[] names;
    private int count;

    public ResourceSupplier(String... names) {
        this.names = Arrays.copyOf(names, names.length);
    }

    @Override
    public BufferedImage get() {
        if (names.length < count) {
            String name = names[count++];
            MinesweeperTrainingBoard board = MinesweeperTrainingBoard.fromResource("minesweeper/" + name);
            return board != null ? board.getImage() : null;
        }
        return null;
    }

}
