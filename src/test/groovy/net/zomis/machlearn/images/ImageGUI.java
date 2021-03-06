package net.zomis.machlearn.images;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ImageGUI extends Application {

    private Spinner<Double> spinX;
    private Spinner<Double> spinY;
    private Spinner<Double> spinWidth;
    private Spinner<Double> spinHeight;
    private ImageView imageView;
    private Image bigImage;
    private MinesweeperScan minesweeperScan;

    private void initSpinners(Image image) {
        spinX = new Spinner<>(0, image.getWidth(), 0);
        spinY = new Spinner<>(0, image.getHeight(), 0);
        spinWidth = new Spinner<>(1, image.getWidth(), 1);
        spinHeight = new Spinner<>(1, image.getHeight(), 1);
        Stream.of(spinX, spinY, spinWidth, spinHeight).forEach(sp -> {
            sp.valueProperty().addListener(this::spinUpdate);
            sp.setEditable(true);
        });
    }

    private void spinUpdate(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        BufferedImage img = SwingFXUtils.fromFXImage(bigImage, null);
        img = Scalr.crop(img, spinX.getValue().intValue(), spinY.getValue().intValue(),
                spinWidth.getValue().intValue(),
                spinHeight.getValue().intValue());
        Image fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    private void clicked(MouseEvent event) {
    }

    @Override
    public void start(Stage primaryStage) {
        /*
        * Select image
        * x Select size
        * Choose training set or create a new one
        * Choose file to run on, or to use a screenshot
        * Groovy script? to specify how to run the network
        * Highlight areas in image depending on Neural Network result
        **/
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
//        String file = "binero/binero.png";
        String file = "minesweeper/challenge-flags-16x16.png";
//        String file = "minesweeper/challenge-press-26x14.png";
        bigImage = new Image(getClass().getClassLoader()
            .getResourceAsStream(file));
        imageView = new ImageView(bigImage);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::clicked);
        FlowPane flowPane = new FlowPane();
        Button button = new Button("Test");
        button.setOnAction(ev -> this.minesweeperScan());
        flowPane.getChildren().add(button);
        initSpinners(bigImage);
        flowPane.getChildren().add(spinX);
        flowPane.getChildren().add(spinY);
        flowPane.getChildren().add(spinWidth);
        flowPane.getChildren().add(spinHeight);
        root.setTop(flowPane);
        root.setCenter(imageView);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void minesweeperScan() {
        String defaultData = "challenge-press-26x14";
        TextInputDialog dialog = new TextInputDialog(defaultData);
        String dataSet = dialog.showAndWait().orElse(defaultData);
        if (dataSet.equals("*")) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(null);
            Set<String> scanned = new HashSet<>();
            for (File file : directory.listFiles()) {
                String fileName = file.getName();
                while (fileName.contains(".")) {
                    fileName = fileName.substring(0, fileName.indexOf("."));
                }
                scanned.add(fileName);
            }
            if (minesweeperScan == null) {
                minesweeperScan = new MinesweeperScan();
            }
            for (String name : scanned) {
                MinesweeperTrainingBoard board = MinesweeperTrainingBoard.fromResource(name);
                minesweeperScan.scan(board);
            }
            return;
        }
        MinesweeperTrainingBoard board = MinesweeperTrainingBoard.fromResource(dataSet);

        if (minesweeperScan == null) {
            minesweeperScan = new MinesweeperScan();
        }
        minesweeperScan.scan(board);
    }

    public static void main(String[] args) {
        launch(ImageGUI.class, args);
    }

}
