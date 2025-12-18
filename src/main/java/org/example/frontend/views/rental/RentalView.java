package org.example.frontend.views.rental;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.entity.Rental;
import org.example.services.RentalService;

import java.util.ArrayList;
import java.util.List;

public class RentalView {
    private final RentalTable rentalTable;
    private final Pane root;

    public RentalView() {
        this.rentalTable = new RentalTable(new ArrayList<>());
        this.root = createRoot();
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Bokning");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        VBox root = new VBox(viewTitle, this.rentalTable.getRoot());
        root.setSpacing(20);

        return root;
    }

    private List<Rental> loadRentals() {
        List<Rental> items;

        try {
            items = RentalService.readAsList();
        } catch (Exception e) {
            items = new ArrayList<>();
        }

        return items;
    }

    public Pane render() {
        this.rentalTable.setItems(loadRentals());

        return root;
    }
}
