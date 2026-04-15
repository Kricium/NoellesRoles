package org.agmas.noellesroles.client.sound;

import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.ConfigWorldComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public final class SoundPhysicsConfigLockManager extends AbstractConfigLockManager {
    private static final String MOD_ID = "sound_physics_remastered";
    private static final String CONFIG_HOLDER_CLASS = "com.sonicether.soundphysics.SoundPhysicsMod";
    private static final String CONFIG_ENTRY_CLASS = "de.maxhenkel.sound_physics_remastered.configbuilder.entry.ConfigEntry";

    private static final SoundPhysicsConfigLockManager INSTANCE = new SoundPhysicsConfigLockManager();

    private SoundPhysicsConfigLockManager() {
    }

    public static void updateFromWorld(ConfigWorldComponent config) { INSTANCE.doUpdateFromWorld(config); }
    public static void tick(MinecraftClient client) { INSTANCE.doTick(client); }
    public static void deactivate() { INSTANCE.doDeactivate(); }

    @Override
    protected String getModId() { return MOD_ID; }

    @Override
    protected boolean isActive(ConfigWorldComponent config) { return config.lockSoundPhysicsRemasteredConfig; }

    @Override
    protected Map<String, String> getLockedValues(ConfigWorldComponent config) { return config.soundPhysicsRemasteredLockedValues; }

    @Override
    protected boolean doApply() {
        try {
            Class<?> holderClass = Class.forName(CONFIG_HOLDER_CLASS);
            Object config = holderClass.getField("CONFIG").get(null);
            if (config == null) {
                return false;
            }

            Class<?> configEntryClass = Class.forName(CONFIG_ENTRY_CLASS);
            Method getMethod = configEntryClass.getMethod("get");
            Method setMethod = configEntryClass.getMethod("set", Object.class);
            Method saveMethod = configEntryClass.getMethod("save");

            boolean changed = false;
            StringBuilder fingerprintBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : lockedValues.entrySet()) {
                Field field;
                try {
                    field = config.getClass().getField(entry.getKey());
                } catch (NoSuchFieldException ignored) {
                    continue;
                }

                Object configEntry = field.get(config);
                if (configEntry == null || !configEntryClass.isInstance(configEntry)) {
                    continue;
                }

                Object currentValue = getMethod.invoke(configEntry);
                Object parsedValue = parseValueFrom(currentValue, entry.getValue());
                if (parsedValue == null) {
                    continue;
                }

                fingerprintBuilder.append(entry.getKey()).append('=').append(parsedValue).append(';');
                if (!Objects.equals(currentValue, parsedValue)) {
                    setMethod.invoke(configEntry, parsedValue);
                    saveMethod.invoke(configEntry);
                    changed = true;
                }
            }

            try {
                config.getClass().getMethod("reloadClient").invoke(config);
            } catch (ReflectiveOperationException ignored) {
                // Best-effort refresh only.
            }

            String newFingerprint = fingerprintBuilder.toString();
            if (changed || !newFingerprint.equals(lastFingerprint)) {
                lastFingerprint = newFingerprint;
            }
            return true;
        } catch (ReflectiveOperationException e) {
            if (lastApplySuccessful) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
