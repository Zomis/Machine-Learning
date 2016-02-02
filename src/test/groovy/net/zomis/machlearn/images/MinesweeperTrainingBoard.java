package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;

public class MinesweeperTrainingBoard {

    private final BufferedImage image;
    private final String expected;
    private final String name;

    public MinesweeperTrainingBoard(String name, BufferedImage image, String expected) {
        this.image = image;
        this.expected = expected;
        this.name = name;
    }

    public static MinesweeperTrainingBoard fromResource(String resourceName) {
        BufferedImage image1 = MyImageUtil.resource(resourceName + ".png");
        String expected = MyGroovyUtils.text(MinesweeperTrainingBoard.class
            .getClassLoader().getResource(resourceName + ".txt"));
        return new MinesweeperTrainingBoard(resourceName, image1, expected);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getExpected() {
        return expected;
    }

    public String getName() {
        return name;
    }

}
