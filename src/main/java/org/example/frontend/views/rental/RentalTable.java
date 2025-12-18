package org.example.frontend.views.rental;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import org.example.entity.Rental;
import org.example.entity.item.Item;
import org.example.entity.member.Member;
import org.example.services.ItemService;
import org.example.services.MemberService;
import org.example.services.RentalService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RentalTable {
    private final SimpleListProperty<Rental> items;
    private final Parent root;
    private final TableView<Rental> table;
    private final SimpleObjectProperty<Rental> selectedRental;

    public RentalTable(List<Rental> items) {
        this.items = new SimpleListProperty<>(FXCollections.observableList(items));
        this.selectedRental = new SimpleObjectProperty<>(null);
        this.table = createTable();
        this.root = createRoot();
    }

    public void setItems(List<Rental> items) {
        this.items.setAll(items);
    }

    private List<Member> loadMembers() {
        try {
            return MemberService.readAsList();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kunde inte ladda listan med medlemmar.");
            alert.setHeaderText(null);
            alert.initOwner(root.getScene().getWindow());
            alert.setX(alert.getOwner().getX());
            alert.setY(alert.getOwner().getY());
            alert.setContentText(
                    "Ett fel inträffade och tabellen kunde inte hittas:\n\nError: "
                            + e.getMessage());
            alert.showAndWait();

            return null;
        }
    }

    private List<Item> loadItems() {
        try {
            return ItemService.readAsList();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kunde inte ladda listan med artiklar.");
            alert.setHeaderText(null);
            alert.initOwner(root.getScene().getWindow());
            alert.setX(alert.getOwner().getX());
            alert.setY(alert.getOwner().getY());
            alert.setContentText(
                    "Ett fel inträffade och tabellen kunde inte hittas:\n\nError: "
                            + e.getMessage());
            alert.showAndWait();

            return null;
        }
    }

    private void showAddRentalModal() {
        List<Member> members = loadMembers();

        if (members == null) {
            return;
        }

        List<Item> items = loadItems();

        if (items == null) {
            return;
        }

        new AddRentalDialog(
                        this.root.getScene().getWindow(),
                        members,
                        items.stream()
                                .filter(Item::isAvailable)
                                .sorted(Comparator.comparing(Item::getName))
                                .collect(Collectors.toList()))
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void showEndRentalDialog() {
        try {
            new EndRentalDialog(
                            this.root.getScene().getWindow(),
                            this.selectedRental.get(),
                            MemberService.getMember(this.selectedRental.get().getMemberId()),
                            ItemService.getItem(this.selectedRental.get().getItemId()))
                    .showAndWait()
                    .ifPresent(
                            (b) -> {
                                if (b) {
                                    updateTableItems();
                                }
                            });
        } catch (Exception e) {
            showErrorAlert(
                    "Kunde inte avsluta bokning.",
                    "Ett fel inträffade och all information som behövdes kunde inte läsas in.",
                    e);
        }
    }

    private void updateTableItems() {
        try {
            this.items.setAll(RentalService.readAsList());
        } catch (Exception e) {
            showErrorAlert(
                    "Kunde inte uppdatera tabellen med bokningen.",
                    "Ett fel inträffade och tabellen kunde inte uppdateras",
                    e);
        }
    }

    private void showErrorAlert(String title, String contentText, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initOwner(root.getScene().getWindow());
        alert.setX(alert.getOwner().getX());
        alert.setY(alert.getOwner().getY());
        alert.setContentText(contentText + "\n\nError: " + e.getMessage());
        alert.showAndWait();
    }

    private Pane createControls() {
        Button createBtn = new Button("Skapa");
        createBtn.setOnAction((e) -> showAddRentalModal());
        Button endBtn = new Button("Avsluta");
        endBtn.disableProperty().bind(this.selectedRental.isNull());
        endBtn.setOnAction((e) -> showEndRentalDialog());

        HBox controlsWrapper = new HBox(createBtn, endBtn);
        controlsWrapper.setSpacing(10);

        HBox tableControls = new HBox(controlsWrapper);

        return tableControls;
    }

    private Parent createRoot() {
        VBox root = new VBox(createControls(), this.table);
        root.setSpacing(10);

        return root;
    }

    private TableView<Rental> createTable() {
        TableColumn<Rental, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Rental, String> memberIdCol = new TableColumn<>("Medlem: ID");
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));

        TableColumn<Rental, String> itemIdCol = new TableColumn<>("Artikel: ID");
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));

        TableColumn<Rental, Boolean> activeCol = new TableColumn<>("Pågående");
        activeCol.setCellValueFactory(
                (cell) -> new SimpleBooleanProperty(cell.getValue().isActive()));

        TableView<Rental> table = new TableView<>();
        table.getColumns().addAll(idCol, memberIdCol, itemIdCol, activeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.itemsProperty().bind(this.items);
        table.selectionModelProperty()
                .get()
                .selectedItemProperty()
                .addListener((e, o, n) -> this.selectedRental.set(n));

        return table;
    }

    public Parent getRoot() {
        return root;
    }
}
