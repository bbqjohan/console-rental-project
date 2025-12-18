package org.example.frontend.views.home;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.entity.Rental;
import org.example.entity.item.Item;
import org.example.entity.member.Member;
import org.example.entity.member.MemberStatus;
import org.example.services.ItemService;
import org.example.services.MemberService;
import org.example.services.RentalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeView {
    private final SimpleListProperty<Item> inventory;
    private final SimpleListProperty<Member> members;
    private final SimpleListProperty<Rental> rentals;
    private final Pane root;
    private final SimpleIntegerProperty casualMembers;
    private final SimpleIntegerProperty gamerMembers;
    private final SimpleIntegerProperty itemsAvailable;
    private final SimpleIntegerProperty activeRentals;
    private final SimpleDoubleProperty revenue;

    public HomeView() {
        this.inventory = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.members = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.rentals = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.casualMembers = new SimpleIntegerProperty(0);
        this.gamerMembers = new SimpleIntegerProperty(0);
        this.itemsAvailable = new SimpleIntegerProperty(0);
        this.activeRentals = new SimpleIntegerProperty(0);
        this.revenue = new SimpleDoubleProperty(0);
        this.root = createRoot();

        this.members.addListener(
                (e, o, n) -> {
                    this.casualMembers.set(
                            n.filtered((m) -> m.getStatus().equals(MemberStatus.CASUAL)).size());
                    this.gamerMembers.set(n.size() - this.casualMembers.get());
                });

        this.inventory.addListener(
                (e, o, n) -> {
                    this.itemsAvailable.set(n.filtered(Item::isAvailable).size());
                });

        this.rentals.addListener(
                (e, o, n) -> {
                    this.activeRentals.set(n.filtered(Rental::isActive).size());

                    List<Rental> inactive = n.filtered((r) -> !r.isActive());
                    double rev = 0;

                    for (Rental r : inactive) {
                        Optional<Member> member =
                                this.members.stream()
                                        .filter((m) -> m.getId() == r.getMemberId())
                                        .findFirst();

                        if (member.isPresent()) {
                            rev +=
                                    member.get()
                                            .getPricePolicy()
                                            .calculatePrice(r.getStartDate(), r.getStopDate());
                        }
                    }

                    this.revenue.set(rev);
                });
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Hem");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        Label membersTitle = new Label("Medlemmar");
        membersTitle.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        Label membersSizeLbl = new Label("Registrerade");
        Label membersSizeInfo = new Label("");
        membersSizeInfo.textProperty().bind(this.members.sizeProperty().asString());
        Label membersCasualsLbl = new Label("Casuals");
        Label membersCasualsInfo = new Label("");
        membersCasualsInfo.textProperty().bind(this.casualMembers.asString());
        Label membersGamersLbl = new Label("Gamers");
        Label membersGamersInfo = new Label("");
        membersGamersInfo.textProperty().bind(this.gamerMembers.asString());
        GridPane membersGrid = new GridPane();
        membersGrid.setHgap(10);
        membersGrid.setVgap(5);
        membersGrid.add(membersSizeLbl, 0, 0);
        membersGrid.add(membersSizeInfo, 1, 0);
        membersGrid.add(membersCasualsLbl, 0, 1);
        membersGrid.add(membersCasualsInfo, 1, 1);
        membersGrid.add(membersGamersLbl, 0, 2);
        membersGrid.add(membersGamersInfo, 1, 2);
        VBox membersRoot = new VBox(membersTitle, membersGrid);
        membersRoot.setSpacing(10);

        Label inventoryTitle = new Label("Lager");
        inventoryTitle.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        Label inventorySizeLbl = new Label("Artiklar");
        Label inventorySizeInfo = new Label("");
        inventorySizeInfo.textProperty().bind(this.inventory.sizeProperty().asString());
        Label inventoryAvailableLbl = new Label("Tillgängliga");
        Label inventoryAvailableInfo = new Label("");
        inventoryAvailableInfo.textProperty().bind(this.itemsAvailable.asString());
        GridPane inventoryGrid = new GridPane();
        inventoryGrid.setHgap(10);
        inventoryGrid.setVgap(5);
        inventoryGrid.add(inventorySizeLbl, 0, 0);
        inventoryGrid.add(inventorySizeInfo, 1, 0);
        inventoryGrid.add(inventoryAvailableLbl, 0, 1);
        inventoryGrid.add(inventoryAvailableInfo, 1, 1);
        VBox inventoryRoot = new VBox(inventoryTitle, inventoryGrid);
        inventoryRoot.setSpacing(10);

        Label rentalsTitle = new Label("Bokningar");
        rentalsTitle.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        Label rentalsActiveLbl = new Label("Aktiva");
        Label rentalsActiveInfo = new Label("");
        rentalsActiveInfo.textProperty().bind(this.activeRentals.asString());
        Label rentalsRevenueLbl = new Label("Intäkter");
        Label rentalsRevenueInfo = new Label("");
        rentalsRevenueInfo.textProperty().bind(this.revenue.asString());
        GridPane rentalsGrid = new GridPane();
        rentalsGrid.setHgap(10);
        rentalsGrid.setVgap(5);
        rentalsGrid.add(rentalsActiveLbl, 0, 0);
        rentalsGrid.add(rentalsActiveInfo, 1, 0);
        rentalsGrid.add(rentalsRevenueLbl, 0, 1);
        rentalsGrid.add(rentalsRevenueInfo, 1, 1);
        VBox rentalsRoot = new VBox(rentalsTitle, rentalsGrid);
        rentalsRoot.setSpacing(10);

        VBox root = new VBox(viewTitle, membersRoot, inventoryRoot, rentalsRoot);
        root.setSpacing(20);

        return root;
    }

    private List<Item> loadInventory() {
        List<Item> items;

        try {
            items = ItemService.readAsList();
        } catch (Exception e) {
            items = new ArrayList<>();
        }

        return items;
    }

    private List<Member> loadMembers() {
        List<Member> members;

        try {
            members = MemberService.readAsList();
        } catch (Exception e) {
            members = new ArrayList<>();
        }

        return members;
    }

    private List<Rental> loadRentals() {
        List<Rental> rentals;

        try {
            rentals = RentalService.readAsList();
        } catch (Exception e) {
            rentals = new ArrayList<>();
        }

        return rentals;
    }

    public Pane render() {
        this.members.setAll(loadMembers());
        this.inventory.setAll(loadInventory());
        this.rentals.setAll(loadRentals());

        return root;
    }
}
