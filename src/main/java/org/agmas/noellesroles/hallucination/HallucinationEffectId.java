package org.agmas.noellesroles.hallucination;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum HallucinationEffectId {
    BASIC_DUMMY("basic_dummy", 1, false, true),
    HIDDEN_BODIES("hidden_bodies", 1, false, true),
    FAKE_TIME("fake_time", 2, true, true),
    FAKE_MONEY("fake_money", 2, true, true),
    FAKE_SANITY("fake_sanity", 2, true, false),
    FAKE_SOUND("fake_sound", 2, false, true),
    KILLER_DUMMY("killer_dummy", 3, false, true),
    HIDDEN_PLAYER("hidden_player", 3, false, true),
    INSTINCT_MISJUDGE("instinct_misjudge", 3, false, true),
    SCRAMBLED_SKINS("scrambled_skins", 4, true, false),
    HIDDEN_UI("hidden_ui", 4, true, true);

    private final String id;
    private final int level;
    private final boolean durationBased;
    private final boolean repeatable;

    HallucinationEffectId(String id, int level, boolean durationBased, boolean repeatable) {
        this.id = id;
        this.level = level;
        this.durationBased = durationBased;
        this.repeatable = repeatable;
    }

    public String id() {
        return this.id;
    }

    public int level() {
        return this.level;
    }

    public boolean isDurationBased() {
        return this.durationBased;
    }

    public boolean isRepeatable() {
        return this.repeatable;
    }

    public static Optional<HallucinationEffectId> byId(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        String normalized = raw.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(value -> value.id.equals(normalized))
                .findFirst();
    }
}
