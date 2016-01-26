package net.zomis.machlearn.images;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class ImageGUI extends Application {

    private Spinner<Double> spinX;
    private Spinner<Double> spinY;
    private Spinner<Double> spinWidth;
    private Spinner<Double> spinHeight;
    private ImageView imageView;
    private Image bigImage;

    private void initSpinners(Image image) {
        spinX = new Spinner<>(0, image.getWidth(), 0);
        spinY = new Spinner<>(0, image.getHeight(), 0);
        spinWidth = new Spinner<>(1, image.getWidth(), 1);
        spinHeight = new Spinner<>(1, image.getHeight(), 1);
        Stream.of(spinX, spinY, spinWidth, spinHeight).forEach(sp ->
                sp.valueProperty().addListener(this::spinUpdate));
    }

    private void spinUpdate(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        BufferedImage img = SwingFXUtils.fromFXImage(bigImage, null);
        img = Scalr.crop(img, spinX.getValue().intValue(), spinY.getValue().intValue(),
                spinWidth.getValue().intValue(),
                spinHeight.getValue().intValue());
        Image fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        bigImage = new Image(getClass().getClassLoader()
            .getResourceAsStream("challenge-flags-16x16.png"));
        imageView = new ImageView(bigImage);
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(new Button("Test"));
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

    public static void main(String[] args) {
        launch(ImageGUI.class, args);
    }

}
