package org.example.frontend.views.inventory;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import org.example.entity.item.*;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;

import java.io.IOException;
import java.util.Arrays;

public class AddItemDialog extends Dialog<Boolean> {
    private final SimpleStringProperty name;
    private final SimpleStringProperty color;
    private final SimpleObjectProperty<ConsoleType> consoleType;
    private final SimpleStringProperty manufacturer;
    private final SimpleBooleanProperty available;
    private final SimpleStringProperty nameError;
    private final SimpleStringProperty colorError;
    private final SimpleStringProperty consoleTypeError;
    private final SimpleStringProperty manufacturerError;
    private final Window window;

    public AddItemDialog(Window window) {
        super();
        this.name = new SimpleStringProperty("");
        this.color = new SimpleStringProperty("");
        this.consoleType = new SimpleObjectProperty<>(null);
        this.manufacturer = new SimpleStringProperty("");
        this.available = new SimpleBooleanProperty(true);
        this.nameError = new SimpleStringProperty("");
        this.colorError = new SimpleStringProperty("");
        this.consoleTypeError = new SimpleStringProperty("");
        this.manufacturerError = new SimpleStringProperty("");
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Ny artikel");
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

        ButtonType saveBtn = new ButtonType("Lägg till", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Skapa ny artikel.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(saveBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (ev) -> {
                            if (!validateForm()) {
                                ev.consume();
                                return;
                            }

                            Item newItem =
                                    consoleType.get().equals(ConsoleType.HANDHELD)
                                            ? new HandheldConsole()
                                            : new StationaryConsole();

                            newItem.setName(name.get());
                            newItem.setColor(color.get());
                            newItem.setAvailable(available.get());
                            newItem.setManufacturer(manufacturer.get());
                            newItem.setConsoleType(consoleType.get());

                            try {
                                ItemService.addNewItem(newItem);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte skapa artikel.",
                                                "Ett fel inträffade och artikeln kunde inte skapas",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == saveBtn);
    }

    private Pane createForm() {
        Label nameLbl = new Label("Namn");
        TextField nameFld = new TextField();
        nameFld.textProperty().bindBidirectional(name);
        nameFld.setPrefWidth(300);
        Label nameErrorLbl = createErrorLabel(nameError);

        Label colorLbl = new Label("Färg");
        TextField colorFld = new TextField();
        colorFld.textProperty().bindBidirectional(color);
        Label colorErrorLbl = createErrorLabel(colorError);

        Label consoleTypeLbl = new Label("Konsoltyp");
        ComboBox<ConsoleType> consoleTypeFld =
                new ComboBox<>(FXCollections.observableList(Arrays.asList(ConsoleType.values())));
        consoleTypeFld.setConverter(
                new StringConverter<ConsoleType>() {
                    @Override
                    public String toString(ConsoleType consoleType) {
                        return consoleType.toLabel();
                    }

                    @Override
                    public ConsoleType fromString(String s) {
                        switch (s) {
                            case "Stationär":
                                return ConsoleType.STATIONARY;
                            case "Handhållen":
                                return ConsoleType.HANDHELD;
                            default:
                                return null;
                        }
                    }
                });
        consoleTypeFld.valueProperty().bindBidirectional(consoleType);
        consoleTypeFld.setMaxWidth(Double.MAX_VALUE);
        Label consoleTypeErrorFld = createErrorLabel(consoleTypeError);

        Label manufacturerLbl = new Label("Tillverkare");
        ComboBox<String> manufacturerFld =
                new ComboBox<>(
                        FXCollections.observableList(
                                Arrays.asList("Nintendo", "Sega", "Sony", "Microsoft")));
        manufacturerFld.valueProperty().bindBidirectional(manufacturer);
        manufacturerFld.setEditable(true);
        manufacturerFld.setMaxWidth(Double.MAX_VALUE);
        Label manufacturerFldErrorFld = createErrorLabel(manufacturerError);

        Label availableLbl = new Label("Tillgänglig");
        CheckBox availableFld = new CheckBox();
        availableFld.selectedProperty().bindBidirectional(available);

        VBox root = new VBox();
        GridPane theOneGrid = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);
        theOneGrid.getColumnConstraints().add(0, cc);

        theOneGrid.setHgap(10);
        theOneGrid.setVgap(5);
        theOneGrid.add(nameLbl, 0, 0);
        theOneGrid.add(nameFld, 1, 0);
        theOneGrid.add(nameErrorLbl, 1, 1);

        theOneGrid.add(colorLbl, 0, 2);
        theOneGrid.add(colorFld, 1, 2);
        theOneGrid.add(colorErrorLbl, 1, 3);

        theOneGrid.add(consoleTypeLbl, 0, 4);
        theOneGrid.add(consoleTypeFld, 1, 4);
        theOneGrid.add(consoleTypeErrorFld, 1, 5);

        theOneGrid.add(manufacturerLbl, 0, 6);
        theOneGrid.add(manufacturerFld, 1, 6);
        theOneGrid.add(manufacturerFldErrorFld, 1, 7);

        theOneGrid.add(availableLbl, 0, 8);
        theOneGrid.add(availableFld, 1, 8);

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

    private boolean validateName() {
        if (this.name.get().isEmpty()) {
            this.nameError.set("Vänligen fyll i ett namn.");
            return false;
        }

        this.nameError.set("");

        return true;
    }

    private boolean validateColor() {
        if (this.color.get().isEmpty()) {
            this.colorError.set("Vänligen fyll i en färg.");
            return false;
        }

        this.colorError.set("");

        return true;
    }

    private boolean validateConsoleType() {
        ConsoleType type = this.consoleType.get();

        if (type == null) {
            this.consoleTypeError.set("Vänligen välj en konsoltyp.");
            return false;
        }

        this.consoleTypeError.set("");

        return true;
    }

    private boolean validateManufacturer() {
        if (this.manufacturer.get().isEmpty()) {
            this.manufacturerError.set("Vänligen fyll i en tillverkare.");
            return false;
        }

        this.manufacturerError.set("");

        return true;
    }

    private boolean validateForm() {
        boolean name = validateName();
        boolean color = validateColor();
        boolean consoleType = validateConsoleType();
        boolean manufacturer = validateManufacturer();

        return name && color && consoleType && manufacturer;
    }
}
