package org.agmas.noellesroles.client.sound;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.ConfigWorldComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SoundPhysicsConfigLockManager {
    private static final String MOD_ID = "sound_physics_remastered";
    private static final String CONFIG_HOLDER_CLASS = "com.sonicether.soundphysics.SoundPhysicsMod";
    private static final String CONFIG_ENTRY_CLASS = "de.maxhenkel.sound_physics_remastered.configbuilder.entry.ConfigEntry";

    private static boolean active;
    private static final Map<String, String> lockedValues = new LinkedHashMap<>();
    private static boolean lastApplySuccessful;
    private static String lastFingerprint = "";

    private SoundPhysicsConfigLockManager() {
    }

    public static void updateFromWorld(ConfigWorldComponent config) {
        if (config == null || !config.lockSoundPhysicsRemasteredConfig) {
            deactivate();
            return;
        }
        active = true;
        lockedValues.clear();
        lockedValues.putAll(config.soundPhysicsRemasteredLockedValues);
        lastFingerprint = "";
        applyLockedConfig();
    }

    public static void tick(MinecraftClient client) {
        if (!active || client == null || client.world == null) {
            return;
        }
        applyLockedConfig();
    }

    public static void deactivate() {
        active = false;
        lockedValues.clear();
        lastFingerprint = "";
        lastApplySuccessful = false;
    }

    private static void applyLockedConfig() {
        if (!FabricLoader.getInstance().isModLoaded(MOD_ID) || lockedValues.isEmpty()) {
            lastApplySuccessful = false;
            return;
        }

        try {
            Class<?> holderClass = Class.forName(CONFIG_HOLDER_CLASS);
            Object config = holderClass.getField("CONFIG").get(null);
            if (config == null) {
                lastApplySuccessful = false;
                return;
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
                Object parsedValue = parseValue(currentValue, entry.getValue());
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
            lastApplySuccessful = true;
        } catch (ReflectiveOperationException e) {
            if (lastApplySuccessful) {
                e.printStackTrace();
            }
            lastApplySuccessful = false;
        }
    }

    private static Object parseValue(Object currentValue, String rawValue) {
        if (currentValue instanceof Boolean) {
            return Boolean.parseBoolean(rawValue);
        }
        if (currentValue instanceof Integer) {
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (currentValue instanceof Long) {
            try {
                return Long.parseLong(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (currentValue instanceof Float) {
            try {
                return Float.parseFloat(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (currentValue instanceof Double) {
            try {
                return Double.parseDouble(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (currentValue instanceof String) {
            return rawValue;
        }
        return null;
    }
}
