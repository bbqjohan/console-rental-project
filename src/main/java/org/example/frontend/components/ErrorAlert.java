package org.example.frontend.components;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class ErrorAlert extends Alert {
    public ErrorAlert(Window window, String title, String content, Exception e) {
        super(AlertType.ERROR);
        this.setTitle(title);
        this.setHeaderText(null);
        this.initOwner(window);
        this.setX(this.getOwner().getX());
        this.setY(this.getOwner().getY());
        this.setContentText(content + "\n\nError: " + e.getMessage());
    }
}
