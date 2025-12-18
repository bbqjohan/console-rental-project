package org.example.frontend.views.member;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import org.example.entity.member.Member;
import org.example.services.MemberService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MembersTable {
    private final SimpleStringProperty searchQuery;
    private final SimpleListProperty<Member> items;
    private final SimpleListProperty<Member> filteredItems;
    private final Parent root;
    private final TableView<Member> table;
    private final SimpleObjectProperty<Member> selectedMember;

    public MembersTable(List<Member> items) {
        this.items = new SimpleListProperty<>(FXCollections.observableList(items));
        this.filteredItems =
                new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>(items)));
        this.items.addListener((e, o, n) -> this.filterItems());
        this.selectedMember = new SimpleObjectProperty<>(null);
        this.searchQuery = new SimpleStringProperty("");
        this.table = createTable();
        this.root = createRoot();
    }

    private void filterItems() {
        this.filteredItems.setAll(
                this.items.stream()
                        .filter(
                                (m) -> {
                                    String query = searchQuery.getValue().toLowerCase();

                                    return m.getFirstname().toLowerCase().contains(query)
                                            || m.getLastname().contains(query)
                                            || m.getEmail().toLowerCase().contains(query)
                                            || String.valueOf(m.getAge()).contains(query)
                                            || String.valueOf(m.getPhone()).contains(query);
                                })
                        .collect(Collectors.toList()));

        if (this.table.getComparator() != null) {
            this.filteredItems.sort(this.table.getComparator());
        }
    }

    private TextField createSearchField() {
        TextField searchFld = new TextField();
        searchFld.setPromptText("Sök medlem...");
        searchFld.textProperty().bindBidirectional(searchQuery);
        searchFld.setOnAction((e) -> this.filterItems());

        return searchFld;
    }

    private void showAddMemberDialog() {
        new AddMemberDialog(this.root.getScene().getWindow())
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void showEditMemberDialog() {
        new EditMemberDialog(this.root.getScene().getWindow(), this.selectedMember.get())
                .showAndWait()
                .ifPresent(
                        (b) -> {
                            if (b) {
                                updateTableItems();
                            }
                        });
    }

    private void showRemoveMemberDialog() {
        new RemoveMemberDialog(this.root.getScene().getWindow(), this.selectedMember.get())
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
            this.items.setAll(MemberService.readAsList());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kunde inte uppdatera tabellen med medlemmar.");
            alert.setHeaderText(null);
            alert.initOwner(root.getScene().getWindow());
            alert.setX(alert.getOwner().getX());
            alert.setY(alert.getOwner().getY());
            alert.setContentText(
                    "Ett fel inträffade och tabellen kunde inte uppdateras:\n\nError: "
                            + e.getMessage());
            alert.showAndWait();
        }
    }

    private Pane createControls() {
        Button searchBtn = new Button("Sök");
        searchBtn.setOnAction((e) -> this.filterItems());
        Button addBtn = new Button("Lägg till");
        addBtn.setOnAction((e) -> showAddMemberDialog());
        Button editBtn = new Button("Redigera");
        editBtn.disableProperty().bind(this.selectedMember.isNull());
        editBtn.setOnAction((e) -> showEditMemberDialog());
        Button removeBtn = new Button("Ta bort");
        removeBtn.disableProperty().bind(this.selectedMember.isNull());
        removeBtn.setOnAction((e) -> showRemoveMemberDialog());

        HBox searchWrapper = new HBox(createSearchField(), searchBtn);
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

    private TableView<Member> createTable() {
        TableColumn<Member, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Member, String> nameCol = new TableColumn<>("Namn");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Member, String>("firstname") {
                    @Override
                    public ObservableValue<String> call(
                            TableColumn.CellDataFeatures<Member, String> param) {
                        String fn = param.getValue().getFirstname();
                        String ln = param.getValue().getLastname();

                        return new SimpleStringProperty(fn + " " + ln);
                    }
                });

        TableColumn<Member, Integer> ageCol = new TableColumn<>("Ålder");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Member, String> phoneCol = new TableColumn<>("Telefon");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Member, String> emailCol = new TableColumn<>("Epost");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Member, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(
                (e) -> new SimpleStringProperty(e.getValue().getStatus().toLabel()));

        TableView<Member> table = new TableView<>();
        table.getColumns().addAll(idCol, nameCol, ageCol, phoneCol, emailCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.itemsProperty().bind(filteredItems);
        table.selectionModelProperty()
                .get()
                .selectedItemProperty()
                .addListener((e, o, n) -> this.selectedMember.set(n));

        return table;
    }

    public Parent getRoot() {
        return root;
    }
}
