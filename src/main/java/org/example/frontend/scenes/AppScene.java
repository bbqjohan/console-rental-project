package org.example.frontend.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.example.frontend.views.home.HomeView;
import org.example.frontend.views.inventory.InventoryView;
import org.example.frontend.views.member.MemberView;
import org.example.frontend.views.rental.RentalView;

public class AppScene {
    private Sidebar sidebar;
    private MemberView memberView;
    private InventoryView inventoryView;
    private RentalView rentalView;
    private HomeView homeView;
    private Pane content;
    private Pane root;
    private Scene scene;

    public AppScene() {
        sidebar = new Sidebar(this::switchContent);
        memberView = new MemberView();
        inventoryView = new InventoryView();
        rentalView = new RentalView();
        homeView = new HomeView();
        content = createContent();
        root = new HBox(sidebar.getRoot(), content);
        scene = new Scene(root);

        switchContent("home");
    }

    private Pane createContent() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #E6E6E6; -fx-padding: 20");
        content.setPrefWidth(Integer.MAX_VALUE);

        return content;
    }

    private void switchContent(String name) {
        if (name.equalsIgnoreCase("members")) {
            content.getChildren().setAll(memberView.render());
        } else if (name.equalsIgnoreCase("inventory")) {
            content.getChildren().setAll(inventoryView.render());
        } else if (name.equalsIgnoreCase("rentals")) {
            content.getChildren().setAll(rentalView.render());
        } else if (name.equalsIgnoreCase("home")) {
            content.getChildren().setAll(homeView.render());
        }
    }

    public Pane getRoot() {
        return root;
    }

    public Scene getScene() {
        return scene;
    }
}
