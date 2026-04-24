package org.agmas.noellesroles.murdermayhem;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.Noellesroles;

public final class FogOfWarMurderMayhemEvent implements MurderMayhemEvent {
    public static final Identifier ID = Identifier.of(Noellesroles.MOD_ID, "murder_mayhem/fog_of_war");
    public static final int INITIAL_FOG_RADIUS = 10;
    public static final int MIN_FOG_RADIUS = 0;
    public static final int MAX_FOG_RADIUS = 12;
    public static final int FOG_ADJUSTMENT_INTERVAL_TICKS = 20 * 60;
    public static final int INSTINCT_EXTRA_DISTANCE = 10;

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public String displayNameKey() {
        return "event.noellesroles.murder_mayhem.fog_of_war";
    }

    @Override
    public String descriptionKey() {
        return "event_description.noellesroles.murder_mayhem.fog_of_war";
    }

    @Override
    public void onRoundInitialized(ServerWorld world, GameWorldComponent gameComponent) {
        MurderMayhemWorldComponent component = MurderMayhemWorldComponent.KEY.get(world);
        component.setFogRadius(INITIAL_FOG_RADIUS);
        component.setFogAdjustmentTicksRemaining(FOG_ADJUSTMENT_INTERVAL_TICKS);
    }

    @Override
    public boolean onRoundTick(ServerWorld world, GameWorldComponent gameComponent) {
        MurderMayhemWorldComponent component = MurderMayhemWorldComponent.KEY.get(world);
        if (!component.tickFogAdjustmentWindow()) {
            return false;
        }

        int delta = switch (world.getRandom().nextInt(4)) {
            case 0 -> -2;
            case 1 -> 0;
            default -> 2;
        };

        component.setFogRadius(MathHelper.clamp(component.getFogRadius() + delta, MIN_FOG_RADIUS, MAX_FOG_RADIUS));
        component.setFogAdjustmentTicksRemaining(FOG_ADJUSTMENT_INTERVAL_TICKS);
        return true;
    }
}
