package org.example.frontend.views.rental;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;

import org.example.entity.Rental;
import org.example.entity.item.*;
import org.example.entity.member.Member;
import org.example.frontend.components.ErrorAlert;
import org.example.services.ItemService;
import org.example.services.RentalService;

import java.io.IOException;

public class EndRentalDialog extends Dialog<Boolean> {
    private final Window window;
    private final Rental rental;
    private final Member member;
    private final Item item;

    public EndRentalDialog(Window window, Rental rental, Member member, Item item) {
        super();
        this.rental = rental;
        this.window = window;
        this.member = member;
        this.item = item;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Avsluta bokning");
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

        ButtonType endBtn = new ButtonType("Avsluta", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Avsluta bokning.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(endBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(endBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (e) -> {
                            this.rental.setActive(false);
                            this.item.setAvailable(true);

                            try {
                                ItemService.updateItem(this.item);
                                RentalService.updateRental(rental);
                            } catch (IOException ex) {
                                e.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte avsluta bokning",
                                                "Ett fel inträffade och bokningen kunde inte avslutas.",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == endBtn);
    }

    private Pane createForm() {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);

        Label memberSectionTitle = new Label("Medlem");
        memberSectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        Label memberIdLbl = new Label("ID");
        Label memberIdInfo = new Label(this.member.getId() + "");
        Label memberNameLbl = new Label("Namn");
        Label memberNameInfo = new Label(this.member.getFullName());
        Label memberStatusLbl = new Label("Status");
        Label memberStatusInfo = new Label(this.member.getStatus().toLabel());
        Label memberEmailLbl = new Label("E-post");
        Label memberEmailInfo = new Label(this.member.getEmail());
        GridPane memberInfoGrid = new GridPane();
        memberInfoGrid.setHgap(10);
        memberInfoGrid.setVgap(5);
        memberInfoGrid.getColumnConstraints().add(0, cc);
        memberInfoGrid.add(memberIdLbl, 0, 0);
        memberInfoGrid.add(memberIdInfo, 1, 0);
        memberInfoGrid.add(memberNameLbl, 0, 1);
        memberInfoGrid.add(memberNameInfo, 1, 1);
        memberInfoGrid.add(memberStatusLbl, 0, 2);
        memberInfoGrid.add(memberStatusInfo, 1, 2);
        memberInfoGrid.add(memberEmailLbl, 0, 3);
        memberInfoGrid.add(memberEmailInfo, 1, 3);
        VBox memberRoot = new VBox(memberSectionTitle, memberInfoGrid);
        memberRoot.setSpacing(10);

        Label itemSectionTitle = new Label("Artikel");
        itemSectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        Label itemIdLbl = new Label("ID");
        Label itemIdInfo = new Label(this.item.getId() + "");
        Label itemNameLbl = new Label("Namn");
        Label itemNameInfo = new Label(this.item.getName());
        Label itemColorLbl = new Label("Färg");
        Label itemColorInfo = new Label(this.item.getColor());
        GridPane itemInfoGrid = new GridPane();
        itemInfoGrid.setHgap(10);
        itemInfoGrid.setVgap(5);
        itemInfoGrid.getColumnConstraints().add(0, cc);
        itemInfoGrid.add(itemIdLbl, 0, 0);
        itemInfoGrid.add(itemIdInfo, 1, 0);
        itemInfoGrid.add(itemNameLbl, 0, 1);
        itemInfoGrid.add(itemNameInfo, 1, 1);
        itemInfoGrid.add(itemColorLbl, 0, 2);
        itemInfoGrid.add(itemColorInfo, 1, 2);
        VBox itemRoot = new VBox(itemSectionTitle, itemInfoGrid);
        itemRoot.setSpacing(10);

        Label rentalSectionTitle = new Label("Bokning");
        rentalSectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        Label rentalIdLbl = new Label("ID");
        Label rentalIdInfo = new Label(this.rental.getId() + "");
        Label rentalStartLbl = new Label("Startdatum");
        Label rentalStartInfo = new Label(this.rental.getStartDate().toString());
        Label rentalStopLbl = new Label("Slutdatum");
        Label rentalStopInfo = new Label(this.rental.getStopDate().toString());
        Label rentalPriceLbl = new Label("Pris");
        Label rentalPriceInfo =
                new Label(
                        this.member
                                        .getPricePolicy()
                                        .calculatePrice(
                                                this.rental.getStartDate(),
                                                this.rental.getStopDate())
                                + "");
        GridPane rentalInfoGrid = new GridPane();
        rentalInfoGrid.setHgap(10);
        rentalInfoGrid.setVgap(5);
        rentalInfoGrid.getColumnConstraints().add(0, cc);
        rentalInfoGrid.add(rentalIdLbl, 0, 0);
        rentalInfoGrid.add(rentalIdInfo, 1, 0);
        rentalInfoGrid.add(rentalStartLbl, 0, 1);
        rentalInfoGrid.add(rentalStartInfo, 1, 1);
        rentalInfoGrid.add(rentalStopLbl, 0, 2);
        rentalInfoGrid.add(rentalStopInfo, 1, 2);
        rentalInfoGrid.add(rentalPriceLbl, 0, 3);
        rentalInfoGrid.add(rentalPriceInfo, 1, 3);
        VBox rentalRoot = new VBox(rentalSectionTitle, rentalInfoGrid);
        rentalRoot.setSpacing(10);

        VBox root = new VBox(memberRoot, itemRoot, rentalRoot);
        root.setSpacing(20);

        return root;
    }
}
