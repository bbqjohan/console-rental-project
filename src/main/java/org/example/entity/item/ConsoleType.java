package org.example.entity.item;

public enum ConsoleType {
    STATIONARY,
    HANDHELD;

    public String toLabel() {
        switch (this) {
            case STATIONARY:
                return "Stationär";
            case HANDHELD:
                return "Handhållen";
            default:
                return "";
        }
    }
}
