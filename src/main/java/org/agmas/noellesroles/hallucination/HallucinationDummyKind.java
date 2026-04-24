package org.agmas.noellesroles.hallucination;

public enum HallucinationDummyKind {
    BASIC,
    KILLER;

    public int hallucinationLevel() {
        return switch (this) {
            case BASIC -> 1;
            case KILLER -> 3;
        };
    }
}
