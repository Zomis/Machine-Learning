package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;

public class MinesweeperTrainingBoard {

    private final BufferedImage image;
    private final String expected;

    public MinesweeperTrainingBoard(BufferedImage image, String expected) {
        this.image = image;
        this.expected = expected;
    }

    public static MinesweeperTrainingBoard fromResource(String resourceName) {
        BufferedImage image1 = MyImageUtil.resource(resourceName + ".png");
        String expected = MyGroovyUtils.text(MinesweeperTrainingBoard.class
            .getClassLoader().getResource(resourceName + ".txt"));
        return new MinesweeperTrainingBoard(image1, expected);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getExpected() {
        return expected;
    }

}
