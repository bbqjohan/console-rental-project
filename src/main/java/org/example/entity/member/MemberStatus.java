package org.example.entity.member;

public enum MemberStatus {
    CASUAL,
    GAMER;

    public String toLabel() {
        switch (this) {
            case CASUAL:
                return "Casual";
            case GAMER:
                return "Gamer";
            default:
                return "";
        }
    }
}
