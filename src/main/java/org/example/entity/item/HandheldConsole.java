package org.example.entity.item;

public class HandheldConsole extends Item {
    private double screenSizeIn;

    public HandheldConsole() {
        super();
    }

    public HandheldConsole(
            String name,
            String color,
            String manufacturer,
            boolean available,
            double screenSizeIn) {
        super();

        setName(name);
        setColor(color);
        setManufacturer(manufacturer);
        setConsoleType(ConsoleType.HANDHELD);
        setAvailable(available);

        this.screenSizeIn = screenSizeIn;
    }

    public HandheldConsole(
            long id,
            String name,
            ConsoleType consoleType,
            String color,
            String manufacturer,
            boolean available,
            double screenSizeIn) {
        super(id, name, consoleType, color, manufacturer, available);

        this.screenSizeIn = screenSizeIn;
    }

    @Override
    public HandheldConsole copy() {
        return new HandheldConsole(
                getId(),
                getName(),
                getConsoleType(),
                getColor(),
                getManufacturer(),
                isAvailable(),
                getScreenSizeIn());
    }

    public double getScreenSizeIn() {
        return screenSizeIn;
    }

    public void setScreenSizeIn(double screenSizeIn) {
        this.screenSizeIn = screenSizeIn;
    }

    @Override
    public String toString() {
        return String.format(
                "{\n\tid: %s,\n\tname: %s,\n\tconsoleType: %s,"
                        + "\n\tcolor: %s,\n\tgameFormat: %s,\n\tscreenSizeIn: %s"
                        + "\n}",
                getId(),
                getName(),
                getConsoleType(),
                getColor(),
                getManufacturer(),
                getScreenSizeIn());
    }
}
