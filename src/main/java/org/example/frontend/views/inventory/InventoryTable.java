package org.example.frontend.views.inventory;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import org.example.entity.item.Item;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryTable {
    private final SimpleStringProperty searchQuery;
    private final SimpleListProperty<Item> items;
    private final SimpleListProperty<Item> filteredItems;
    private final Parent root;
    private final TableView<Item> table;
    private final SimpleObjectProperty<Item> selectedItem;
    private final SimpleBooleanProperty queryOnlyAvailable;

    public InventoryTable(List<Item> items) {
        this.items = new SimpleListProperty<>(FXCollections.observableList(items));
        this.filteredItems =
                new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>(items)));
        this.items.addListener((e, o, n) -> this.filterItems());
        this.selectedItem = new SimpleObjectProperty<>(null);
        this.searchQuery = new SimpleStringProperty("");
        this.queryOnlyAvailable = new SimpleBooleanProperty(false);
        this.table = createTable();
        this.root = createRoot();
    }

    public void setItems(List<Item> items) {
        this.items.setAll(items);
    }

    private void filterItems() {
        this.filteredItems.setAll(
                this.items.stream()
                        .filter(
                                (m) -> {
                                    String query = searchQuery.getValue().toLowerCase();

                                    return (!queryOnlyAvailable.get() || m.isAvailable())
                                            && (m.getName().toLowerCase().contains(query)
                                                    || m.getColor().contains(query)
                                                    || m.getManufacturer()
                                                            .toLowerCase()
                                                            .contains(query)
                                                    || m.getConsoleType()
                                                            .toString()
                                                            .toLowerCase()
                                                            .contains(query));
                                })
                        .collect(Collectors.toList()));

        if (this.table.getComparator() != null) {
            this.filteredItems.sort(this.table.getComparator());
        }
    }

    private TextField createSearchField() {
        TextField searchFld = new TextField();
        searchFld.setPromptText("Sök artikel...");
        searchFld.textProperty().bindBidirectional(searchQuery);
        searchFld.setOnAction((e) -> this.filterItems());

        return searchFld;
    }

    private void showAddItemDialog() {
        new AddItemDialog(this.root.getScene().getWindow())
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void showEditItemDialog() {
        new EditItemDialog(this.root.getScene().getWindow(), this.selectedItem.get())
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void showRemoveItemDialog() {
        new RemoveItemDialog(this.root.getScene().getWindow(), this.selectedItem.get())
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void updateTableItems() {
        try {
            this.items.setAll(ItemService.readAsList());
        } catch (Exception e) {
            new ErrorAlert(root.getScene().getWindow(), "Kunde inte uppdatera tabellen med medlemmar.", "Ett fel inträffade och tabellen kunde inte uppdateras", e).showAndWait();
        }
    }

    private Pane createControls() {
        Button searchBtn = new Button("Sök");
        searchBtn.setOnAction((e) -> filterItems());
        Button addBtn = new Button("Lägg till");
        addBtn.setOnAction((e) -> showAddItemDialog());
        Button editBtn = new Button("Redigera");
        editBtn.disableProperty().bind(this.selectedItem.isNull());
        editBtn.setOnAction((e) -> showEditItemDialog());
        Button removeBtn = new Button("Ta bort");
        removeBtn.disableProperty().bind(this.selectedItem.isNull());
        removeBtn.setOnAction((e) -> showRemoveItemDialog());

        CheckBox availableChk = new CheckBox("Endast tillgängliga");
        availableChk.selectedProperty().bindBidirectional(queryOnlyAvailable);

        HBox searchWrapper = new HBox(createSearchField(), searchBtn, availableChk);
        searchWrapper.setSpacing(10);
        HBox.setHgrow(searchWrapper, Priority.ALWAYS);

        HBox controlsWrapper = new HBox(addBtn, editBtn, removeBtn);
        controlsWrapper.setSpacing(10);

        HBox tableControls = new HBox(searchWrapper, controlsWrapper);

        return tableControls;
    }

    private Parent createRoot() {
        VBox root = new VBox(createControls(), this.table);
        root.setSpacing(10);

        return root;
    }

    private TableView<Item> createTable() {
        TableColumn<Item, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Item, String> nameCol = new TableColumn<>("Namn");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, String> consoleTypeCol = new TableColumn<>("Konsoltyp");
        consoleTypeCol.setCellValueFactory(
                (cell) -> new SimpleStringProperty(cell.getValue().getConsoleType().toLabel()));

        TableColumn<Item, String> colorCol = new TableColumn<>("Färg");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));

        TableColumn<Item, String> manufacturerCol = new TableColumn<>("Tillverkare");
        manufacturerCol.setCellValueFactory(
                (cell) -> new SimpleStringProperty(cell.getValue().getManufacturer()));

        TableColumn<Item, Boolean> availableCol = new TableColumn<>("Tillgänglig");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableView<Item> table = new TableView<>();
        table.getColumns()
                .addAll(idCol, nameCol, consoleTypeCol, colorCol, manufacturerCol, availableCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.itemsProperty().bind(filteredItems);
        table.selectionModelProperty()
                .get()
                .selectedItemProperty()
                .addListener((e, o, n) -> this.selectedItem.set(n));

        return table;
    }

    public Parent getRoot() {
        return root;
    }
}
