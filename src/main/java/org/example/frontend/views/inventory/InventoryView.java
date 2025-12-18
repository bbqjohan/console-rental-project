package org.example.frontend.views.inventory;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.entity.item.Item;
import org.example.services.ItemService;

import java.util.ArrayList;
import java.util.List;

public class InventoryView {
    private final InventoryTable inventoryTable;
    private final Pane root;

    public InventoryView() {
        this.inventoryTable = new InventoryTable(new ArrayList<>());
        this.root = createRoot();
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Lager");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        VBox root = new VBox(viewTitle, this.inventoryTable.getRoot());
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

    public Pane render() {
        this.inventoryTable.setItems(loadInventory());

        return root;
    }
}
