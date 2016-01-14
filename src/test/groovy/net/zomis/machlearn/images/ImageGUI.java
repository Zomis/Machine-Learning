package net.zomis.machlearn.images;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageGUI {

    public static void showImage(BufferedImage image) {
        JFrame frame = new JFrame();
        BufferedImage tempImage = image.getSubimage(834, 284, 39, 39);
        frame.getContentPane().add(new JLabel(new ImageIcon(tempImage)));
        frame.pack();
        frame.setVisible(true);
    }

}
