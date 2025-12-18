package org.example.frontend.views.inventory;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import org.example.entity.item.ConsoleType;
import org.example.entity.item.HandheldConsole;
import org.example.entity.item.Item;
import org.example.entity.item.StationaryConsole;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;

import java.io.IOException;
import java.util.Arrays;

public class EditItemDialog extends Dialog<Boolean> {
    private final SimpleStringProperty name;
    private final SimpleStringProperty color;
    private final SimpleObjectProperty<ConsoleType> consoleType;
    private final SimpleStringProperty controllerPorts;
    private final SimpleStringProperty screenSize;
    private final SimpleStringProperty manufacturer;
    private final SimpleBooleanProperty available;
    private final SimpleStringProperty nameError;
    private final SimpleStringProperty colorError;
    private final SimpleStringProperty consoleTypeError;
    private final SimpleStringProperty manufacturerError;
    private final SimpleStringProperty controllerPortsError;
    private final SimpleStringProperty screenSizeError;
    private final Window window;
    private final Item item;

    public EditItemDialog(Window window, Item item) {
        super();
        this.item = item.copy();
        this.name = new SimpleStringProperty(this.item.getName());
        this.color = new SimpleStringProperty(this.item.getColor());
        this.consoleType = new SimpleObjectProperty<>(this.item.getConsoleType());
        this.controllerPorts =
                new SimpleStringProperty(
                        this.item instanceof StationaryConsole
                                ? ((StationaryConsole) this.item).getControllerPorts() + ""
                                : "");
        this.screenSize =
                new SimpleStringProperty(
                        this.item instanceof HandheldConsole
                                ? ((HandheldConsole) this.item).getScreenSizeIn() + ""
                                : "");
        this.manufacturer = new SimpleStringProperty(this.item.getManufacturer());
        this.available = new SimpleBooleanProperty(this.item.isAvailable());
        this.nameError = new SimpleStringProperty("");
        this.colorError = new SimpleStringProperty("");
        this.consoleTypeError = new SimpleStringProperty("");
        this.manufacturerError = new SimpleStringProperty("");
        this.controllerPortsError = new SimpleStringProperty("");
        this.screenSizeError = new SimpleStringProperty("");
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Redigera arrtikel");
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

        ButtonType saveBtn = new ButtonType("Spara", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Redigera artikel.");
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

                            Item newItem;

                            if (consoleType.get().equals(ConsoleType.STATIONARY)) {
                                newItem = new StationaryConsole();
                                ((StationaryConsole) newItem)
                                        .setControllerPorts(
                                                Integer.parseInt(controllerPorts.get()));

                            } else {
                                newItem = new HandheldConsole();
                                ((HandheldConsole) newItem)
                                        .setScreenSizeIn(Double.parseDouble(screenSize.get()));
                            }

                            newItem.setName(name.get());
                            newItem.setColor(color.get());
                            newItem.setAvailable(available.get());
                            newItem.setManufacturer(manufacturer.get());
                            newItem.setConsoleType(consoleType.get());
                            newItem.setId(this.item.getId());

                            try {
                                ItemService.updateItem(newItem);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte redigera artikeln.",
                                                "Ett fel inträffade och artikeln kunde inte sparas",
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

        Label controllerPortLbl = new Label("Kontrollerportar");
        TextField controllerPortFld = new TextField();
        controllerPortFld.textProperty().bindBidirectional(controllerPorts);
        controllerPortFld.disableProperty().bind(consoleType.isNotEqualTo(ConsoleType.STATIONARY));
        Label controllerPortErrorLbl = createErrorLabel(controllerPortsError);

        Label screenSizeLbl = new Label("Skärmstorlek (in)");
        TextField screenSizeFld = new TextField();
        screenSizeFld.textProperty().bindBidirectional(screenSize);
        screenSizeFld.disableProperty().bind(consoleType.isNotEqualTo(ConsoleType.HANDHELD));
        Label screenSizeErrorLbl = createErrorLabel(screenSizeError);

        Label manufacturerLbl = new Label("Tillverkare");
        ComboBox<String> manufacturerFld =
                new ComboBox<>(
                        FXCollections.observableList(
                                Arrays.asList("Nintendo", "Sega", "Sony", "Microsoft")));
        manufacturerFld.valueProperty().bindBidirectional(manufacturer);
        manufacturerFld.setEditable(true);
        manufacturerFld.setMaxWidth(Double.MAX_VALUE);
        manufacturerFld.valueProperty().set(this.item.getManufacturer());
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

        theOneGrid.add(controllerPortLbl, 0, 6);
        theOneGrid.add(controllerPortFld, 1, 6);
        theOneGrid.add(controllerPortErrorLbl, 1, 7);

        theOneGrid.add(screenSizeLbl, 0, 8);
        theOneGrid.add(screenSizeFld, 1, 8);
        theOneGrid.add(screenSizeErrorLbl, 1, 9);

        theOneGrid.add(manufacturerLbl, 0, 10);
        theOneGrid.add(manufacturerFld, 1, 10);
        theOneGrid.add(manufacturerFldErrorFld, 1, 11);

        theOneGrid.add(availableLbl, 0, 12);
        theOneGrid.add(availableFld, 1, 12);

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

    private boolean validateControllerPorts() {
        String ports = this.controllerPorts.get();

        if (ports.isEmpty()) {
            this.controllerPortsError.set("Konsolen måste minst ha en port.");
            return false;
        }

        try {
            Integer.parseInt(ports);
        } catch (NumberFormatException e) {
            this.controllerPortsError.set("Värdet är inte ett heltal.");
            return false;
        }

        this.controllerPortsError.set("");

        return true;
    }

    private boolean validateScreenSize() {
        String size = this.screenSize.get();

        if (size.isEmpty()) {
            this.controllerPortsError.set("Konsolen måste ha en skärmstorlek.");
            return false;
        }

        try {
            Double.parseDouble(size);
        } catch (NumberFormatException e) {
            this.screenSizeError.set("Värdet är inte ett decimaltal.");
            return false;
        }

        this.screenSizeError.set("");

        return true;
    }

    private boolean validateForm() {
        boolean name = validateName();
        boolean color = validateColor();
        boolean consoleType = validateConsoleType();
        boolean manufacturer = validateManufacturer();
        boolean ports = validateControllerPorts();
        boolean screenSize = validateScreenSize();

        return name
                && color
                && consoleType
                && manufacturer
                && (!this.consoleType.get().equals(ConsoleType.STATIONARY) || ports)
                && (!this.consoleType.get().equals(ConsoleType.HANDHELD) || screenSize);
    }
}
