package org.agmas.noellesroles.murdermayhem;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MurderMayhemEventRegistry {
    private static final List<MurderMayhemEvent> EVENTS = new ArrayList<>();
    private static final MurderMayhemEvent FALLBACK_EVENT = new SimpleMurderMayhemEvent(
            Identifier.of(Noellesroles.MOD_ID, "murder_mayhem/fallback"),
            "event.noellesroles.murder_mayhem.unknown",
            "event_description.noellesroles.murder_mayhem.unknown"
    );

    static {
        register(new FogOfWarMurderMayhemEvent());
    }

    private MurderMayhemEventRegistry() {
    }

    public static MurderMayhemEvent register(MurderMayhemEvent event) {
        EVENTS.add(Objects.requireNonNull(event, "event"));
        return event;
    }

    public static List<MurderMayhemEvent> getAll() {
        return List.copyOf(EVENTS);
    }

    public static MurderMayhemEvent get(Identifier id) {
        if (id == null) {
            return null;
        }
        for (MurderMayhemEvent event : EVENTS) {
            if (event.id().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public static MurderMayhemEvent selectRandom(ServerWorld world) {
        if (EVENTS.isEmpty()) {
            return FALLBACK_EVENT;
        }
        return EVENTS.get(world.getRandom().nextInt(EVENTS.size()));
    }

    private record SimpleMurderMayhemEvent(Identifier id, String displayNameKey, String descriptionKey) implements MurderMayhemEvent {
    }
}
