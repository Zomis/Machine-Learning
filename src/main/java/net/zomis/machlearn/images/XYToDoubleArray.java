package net.zomis.machlearn.images;

import java.awt.image.BufferedImage;

public interface XYToDoubleArray {

    double[] toInput(ImageNetwork network, BufferedImage image, int x, int y);

}
