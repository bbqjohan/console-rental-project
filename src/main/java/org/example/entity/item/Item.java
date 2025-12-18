package org.example.entity.item;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.example.registry.table.Entity;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "consoleType",
        visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = HandheldConsole.class, name = "HANDHELD"),
    @JsonSubTypes.Type(value = StationaryConsole.class, name = "STATIONARY")
})
public abstract class Item extends Entity<Item> {
    private String name;
    private ConsoleType consoleType;
    private String color;
    private String manufacturer;
    private boolean available;

    public Item() {}

    public Item(
            long id,
            String name,
            ConsoleType consoleType,
            String color,
            String manufacturer,
            boolean available) {
        super(id);

        this.name = name;
        this.consoleType = consoleType;
        this.color = color;
        this.manufacturer = manufacturer;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConsoleType getConsoleType() {
        return consoleType;
    }

    public void setConsoleType(ConsoleType consoleType) {
        this.consoleType = consoleType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
