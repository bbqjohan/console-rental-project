package org.example.frontend.views.rental;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Window;

import org.example.entity.Rental;
import org.example.entity.item.*;
import org.example.entity.member.Member;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;
import org.example.services.RentalService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AddRentalDialog extends Dialog<Boolean> {
    private final SimpleObjectProperty<Member> selectedMember;
    private final SimpleObjectProperty<Item> selectedItem;
    private final SimpleObjectProperty<LocalDate> stopDate;
    private final SimpleObjectProperty<LocalDate> startDate;
    private final SimpleStringProperty memberError;
    private final SimpleStringProperty itemError;
    private final SimpleStringProperty stopDateError;
    private final SimpleStringProperty startDateError;
    private final List<Member> members;
    private final List<Item> items;
    private final DoubleBinding priceTotal;
    private final Window window;

    public AddRentalDialog(Window window, List<Member> members, List<Item> items) {
        super();
        this.selectedMember = new SimpleObjectProperty<>(null);
        this.selectedItem = new SimpleObjectProperty<>(null);
        this.startDate = new SimpleObjectProperty<>(LocalDate.now());
        this.stopDate = new SimpleObjectProperty<>(null);
        this.memberError = new SimpleStringProperty("");
        this.itemError = new SimpleStringProperty("");
        this.stopDateError = new SimpleStringProperty("");
        this.startDateError = new SimpleStringProperty("");
        this.members = members;
        this.items = items;
        this.window = window;

        this.priceTotal =
                new DoubleBinding() {
                    {
                        super.bind(selectedMember, selectedItem, startDate, stopDate);
                    }

                    @Override
                    protected double computeValue() {
                        if (selectedMember.get() != null
                                && selectedItem.get() != null
                                && startDate.get() != null
                                && stopDate.get() != null) {
                            return selectedMember
                                    .get()
                                    .getPricePolicy()
                                    .calculatePrice(startDate.get(), stopDate.get());
                        }
                        return 0;
                    }
                };

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Ny bokning");
        formTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold");

        VBox root = new VBox();
        root.getChildren().addAll(formTitle, createForm());
        root.setSpacing(20);
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        ButtonType createBtn = new ButtonType("Skapa", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Skapa ny bokning.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(createBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (ev) -> {
                            if (!validateForm()) {
                                ev.consume();
                                return;
                            }

                            Rental rental = new Rental();
                            rental.setMemberId(selectedMember.get().getId());
                            rental.setItemId(selectedItem.get().getId());
                            rental.setMemberStatus(selectedMember.get().getStatus());
                            rental.setStartDate(startDate.get());
                            rental.setStopDate(stopDate.get());
                            rental.setActive(true);

                            selectedItem.get().setAvailable(false);

                            try {
                                ItemService.updateItem(selectedItem.get());
                                RentalService.addRental(rental);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte skapa bokning.",
                                                "Ett fel inträffade och bokningen kunde inte skapas.",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == createBtn);
    }

    private Pane createForm() {
        Label memberLbl = new Label("Medlem");
        ComboBox<Member> memberFld = new ComboBox<>(FXCollections.observableList(this.members));
        memberFld.setCellFactory((view) -> createMemberCell());
        memberFld.setButtonCell(createMemberCell());
        memberFld.valueProperty().bindBidirectional(selectedMember);
        memberFld.setMaxWidth(Double.MAX_VALUE);
        Label memberErrorLbl = createErrorLabel(memberError);

        Label itemLbl = new Label("Artikel");
        ComboBox<Item> itemFld = new ComboBox<>(FXCollections.observableList(this.items));
        itemFld.setCellFactory((view) -> createItemCell());
        itemFld.setButtonCell(createItemCell());
        itemFld.valueProperty().bindBidirectional(selectedItem);
        itemFld.setMaxWidth(Double.MAX_VALUE);
        Label itemErrorLbl = createErrorLabel(itemError);

        Label startDateLbl = new Label("Startdatum");
        DatePicker startDateFld = new DatePicker();
        startDateFld.valueProperty().bindBidirectional(startDate);
        startDateFld.setMaxWidth(Double.MAX_VALUE);
        Label startDateError = createErrorLabel(this.startDateError);

        Label stopDateLbl = new Label("Slutdatum");
        DatePicker stopDateFld = new DatePicker();
        stopDateFld.setMaxWidth(Double.MAX_VALUE);
        stopDateFld.valueProperty().bindBidirectional(stopDate);
        Label stopDateError = createErrorLabel(this.stopDateError);

        Label priceLbl = new Label("Pris");
        Text priceTxt = new Text(priceTotal.get() + " kr");
        priceTotal.addListener((o, ov, nv) -> priceTxt.textProperty().set(nv.toString() + " kr"));

        VBox root = new VBox();
        GridPane theOneGrid = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);
        theOneGrid.getColumnConstraints().add(0, cc);

        theOneGrid.setHgap(10);
        theOneGrid.setVgap(5);

        theOneGrid.add(memberLbl, 0, 0);
        theOneGrid.add(memberFld, 1, 0);
        theOneGrid.add(memberErrorLbl, 1, 1);

        theOneGrid.add(itemLbl, 0, 2);
        theOneGrid.add(itemFld, 1, 2);
        theOneGrid.add(itemErrorLbl, 1, 3);

        theOneGrid.add(startDateLbl, 0, 4);
        theOneGrid.add(startDateFld, 1, 4);
        theOneGrid.add(startDateError, 1, 5);

        theOneGrid.add(stopDateLbl, 0, 6);
        theOneGrid.add(stopDateFld, 1, 6);
        theOneGrid.add(stopDateError, 1, 7);

        theOneGrid.add(priceLbl, 0, 8);
        theOneGrid.add(priceTxt, 1, 8);

        root.getChildren().add(theOneGrid);

        return root;
    }

    private Label createErrorLabel(SimpleStringProperty stringProp) {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-padding: 0 0 5 0;");
        label.textProperty().bind(stringProp);
        label.managedProperty().bind(stringProp.isNotEmpty());
        label.setWrapText(true);

        return label;
    }

    private ListCell<Member> createMemberCell() {
        return new ListCell<Member>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);
                setText(
                        member == null || empty
                                ? null
                                : member.getId()
                                        + " - "
                                        + member.getFirstname()
                                        + " "
                                        + member.getLastname()
                                        + " - "
                                        + member.getEmail());
            }
        };
    }

    private ListCell<Item> createItemCell() {
        return new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(
                        item == null || empty
                                ? null
                                : item.getId() + " - " + item.getName() + " - " + item.getColor());
            }
        };
    }

    private boolean validateMember() {
        if (this.selectedMember.get() == null) {
            this.memberError.set("Vänligen välj en medlem.");
            return false;
        }

        this.memberError.set("");

        return true;
    }

    private boolean validateItem() {
        if (this.selectedItem.get() == null) {
            this.itemError.set("Vänligen välj en artikel.");
            return false;
        }

        this.itemError.set("");

        return true;
    }

    private boolean validateStartDate() {
        LocalDate start = this.startDate.get();
        LocalDate stop = this.stopDate.get();

        if (start == null) {
            this.startDateError.set("Vänligen välj ett startdatum.");
            return false;
        }

        if (stop != null && start.isAfter(stop)) {
            this.startDateError.set("Startdatumet måste vara minst en dag bakom slutdatumet.");
            return false;
        }

        this.startDateError.set("");

        return true;
    }

    private boolean validateStopDate() {
        LocalDate start = this.startDate.get();
        LocalDate stop = this.stopDate.get();

        if (stop == null) {
            this.stopDateError.set("Vänligen välj ett slutdatum.");
            return false;
        }

        if (start != null && stop.isBefore(start)) {
            this.stopDateError.set("Slutdatumet måste vara minst en dag framför startdatumet.");
            return false;
        }

        this.stopDateError.set("");

        return true;
    }

    private boolean validateForm() {
        boolean member = validateMember();
        boolean item = validateItem();
        boolean start = validateStartDate();
        boolean stop = validateStopDate();

        return member && item && start && stop;
    }
}
