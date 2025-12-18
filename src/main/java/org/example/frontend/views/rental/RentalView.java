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
        List<Rental> rentals;

        try {
            rentals = RentalService.readAsList();
        } catch (Exception e) {
            rentals = new ArrayList<>();
        }

        this.rentalTable = new RentalTable(rentals);
        this.root = createRoot();
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Bokning");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        VBox root = new VBox(viewTitle, this.rentalTable.getRoot());
        root.setSpacing(20);

        return root;
    }

    public Pane render() {
        return root;
    }
}
