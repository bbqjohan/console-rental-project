package org.example;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.example.frontend.scenes.AppScene;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        AppScene appScene = new AppScene();

        stage.setScene(appScene.getScene());
        stage.setWidth(1000);
        stage.setHeight(500);
        stage.setTitle("My app!");
        stage.setX(
                primaryScreenBounds.getMinX()
                        + primaryScreenBounds.getWidth() / 2
                        - stage.getWidth() / 2);
        stage.setY(
                primaryScreenBounds.getMinY()
                        + primaryScreenBounds.getHeight() / 2
                        - stage.getHeight() / 2);
        stage.show();
    }
}
