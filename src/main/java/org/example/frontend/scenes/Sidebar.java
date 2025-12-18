package org.example.frontend.scenes;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Sidebar {
    private OnViewChange onViewChange;
    private Pane root;

    public Sidebar(OnViewChange onViewChange) {
        this.onViewChange = onViewChange;
        this.root = createRoot();
    }

    private Pane createRoot() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: white; -fx-padding: 20;");
        root.setMinWidth(130);
        root.setMaxWidth(130);
        root.setSpacing(20);

        root.getChildren().add(createHomeButton());
        root.getChildren().add(createMemberButton());
        root.getChildren().add(createInventoryButton());
        root.getChildren().add(createRentalButton());

        return root;
    }

    private Node createMemberButton() {
        Button button = new Button("Medlemmar");
        button.setOnAction((e) -> onViewChange.run("members"));

        return button;
    }

    private Node createInventoryButton() {
        Button button = new Button("Lager");
        button.setOnAction((e) -> onViewChange.run("inventory"));

        return button;
    }

    private Node createRentalButton() {
        Button button = new Button("Hyra ut");
        button.setOnAction((e) -> onViewChange.run("rentals"));

        return button;
    }

    private Node createHomeButton() {
        Button button = new Button("Hem");
        button.setOnAction((e) -> onViewChange.run("home"));

        return button;
    }

    public Pane getRoot() {
        return root;
    }

    public interface OnViewChange {
        void run(String name);
    }
}
