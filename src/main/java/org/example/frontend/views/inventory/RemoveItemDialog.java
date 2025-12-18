package org.example.frontend.views.inventory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Window;

import org.example.entity.item.HandheldConsole;
import org.example.entity.item.Item;
import org.example.entity.item.StationaryConsole;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;

import java.io.IOException;

public class RemoveItemDialog extends Dialog<Boolean> {
    private final Window window;
    private final Item item;

    public RemoveItemDialog(Window window, Item item) {
        super();
        this.item = item.copy();
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Radera artikel");
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

        ButtonType deleteBtn = new ButtonType("Radera", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Radera artikel.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(deleteBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(deleteBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (ev) -> {
                            try {
                                ItemService.removeItem(this.item);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte radera artikeln.",
                                                "Ett fel inträffade och artikeln kunde inte raderas",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == deleteBtn);
    }

    private Pane createForm() {
        Label idLbl = new Label("ID");
        Text idTxt = new Text(String.valueOf(this.item.getId()));

        Label nameLbl = new Label("namn");
        Text nameTxt = new Text(this.item.getName());

        Label colorLbl = new Label("Färg");
        Text colorTxt = new Text(this.item.getColor());

        Label consoleTypeLbl = new Label("Konsoltyp");
        Text consoleTypeTxt = new Text(this.item.getConsoleType().toLabel());

        Label manufacturerLbl = new Label("Tillverkare");
        Text manufacturerTxt = new Text(this.item.getManufacturer());

        Label availableLbl = new Label("Tillgänglig");
        Text availableTxt = new Text(this.item.isAvailable() + "");

        VBox root = new VBox();
        GridPane theOneGrid = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);
        theOneGrid.getColumnConstraints().add(0, cc);

        theOneGrid.setHgap(10);
        theOneGrid.setVgap(5);

        theOneGrid.add(idLbl, 0, 0);
        theOneGrid.add(idTxt, 1, 0);

        theOneGrid.add(nameLbl, 0, 1);
        theOneGrid.add(nameTxt, 1, 1);

        theOneGrid.add(colorLbl, 0, 2);
        theOneGrid.add(colorTxt, 1, 2);

        theOneGrid.add(consoleTypeLbl, 0, 3);
        theOneGrid.add(consoleTypeTxt, 1, 3);

        theOneGrid.add(manufacturerLbl, 0, 4);
        theOneGrid.add(manufacturerTxt, 1, 4);

        if (this.item instanceof HandheldConsole) {
            Label screenSizeLbl = new Label("Skärmstorlek");
            Text screenSizeTxt = new Text(((HandheldConsole) this.item).getScreenSizeIn() + "");

            theOneGrid.add(screenSizeLbl, 0, 5);
            theOneGrid.add(screenSizeTxt, 1, 5);
        } else if (this.item instanceof StationaryConsole) {
            Label screenSizeLbl = new Label("Kontrollerportar");
            Text screenSizeTxt =
                    new Text(((StationaryConsole) this.item).getControllerPorts() + "");

            theOneGrid.add(screenSizeLbl, 0, 5);
            theOneGrid.add(screenSizeTxt, 1, 5);
        }

        theOneGrid.add(availableLbl, 0, 6);
        theOneGrid.add(availableTxt, 1, 6);

        root.getChildren().add(theOneGrid);

        return root;
    }
}
