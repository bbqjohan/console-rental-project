package org.example.entity.item;

public class StationaryConsole extends Item {
    private int controllerPorts;

    public StationaryConsole() {
        super();
    }

    public StationaryConsole(
            String name,
            String manufacturer,
            String color,
            boolean available,
            int controllerPorts) {
        super();

        setName(name);
        setManufacturer(manufacturer);
        setColor(color);
        setConsoleType(ConsoleType.STATIONARY);
        setAvailable(available);

        this.controllerPorts = controllerPorts;
    }

    public StationaryConsole(
            long id,
            String name,
            ConsoleType consoleType,
            String color,
            String manufacturer,
            boolean available,
            int controllerPorts) {
        super(id, name, consoleType, color, manufacturer, available);
        this.controllerPorts = controllerPorts;
    }

    @Override
    public StationaryConsole copy() {
        return new StationaryConsole(
                getId(),
                getName(),
                getConsoleType(),
                getColor(),
                getManufacturer(),
                isAvailable(),
                getControllerPorts());
    }

    public int getControllerPorts() {
        return controllerPorts;
    }

    public void setControllerPorts(int controllerPorts) {
        this.controllerPorts = controllerPorts;
    }

    @Override
    public String toString() {
        return String.format(
                "{\n\tid: %s,\n\tname: %s,\n\tconsoleType: %s,"
                        + "\n\tcolor: %s,\n\tgameFormat: %s,\n\tcontrollerPorts: %s"
                        + "\n}",
                getId(),
                getName(),
                getConsoleType(),
                getColor(),
                getManufacturer(),
                getControllerPorts());
    }
}
