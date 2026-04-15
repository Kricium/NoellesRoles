package org.agmas.noellesroles.client.sound;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.ConfigWorldComponent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared state machine for locking an external mod's config values to
 * server-synced entries. Subclasses supply the mod id and reflection-based
 * apply strategy.
 */
public abstract class AbstractConfigLockManager {
    protected boolean active;
    protected final Map<String, String> lockedValues = new LinkedHashMap<>();
    protected boolean lastApplySuccessful;
    protected String lastFingerprint = "";

    protected abstract String getModId();

    protected abstract boolean isActive(ConfigWorldComponent config);

    protected abstract Map<String, String> getLockedValues(ConfigWorldComponent config);

    /**
     * Perform the actual reflective config write. Return true if fingerprint/state updated successfully.
     */
    protected abstract boolean doApply();

    protected final void doUpdateFromWorld(ConfigWorldComponent config) {
        if (config == null || !isActive(config)) {
            doDeactivate();
            return;
        }
        active = true;
        lockedValues.clear();
        lockedValues.putAll(getLockedValues(config));
        lastFingerprint = "";
        applyLockedConfig();
    }

    protected final void doTick(MinecraftClient client) {
        if (!active || client == null || client.world == null) {
            return;
        }
        applyLockedConfig();
    }

    protected final void doDeactivate() {
        active = false;
        lockedValues.clear();
        lastFingerprint = "";
        lastApplySuccessful = false;
    }

    protected final void applyLockedConfig() {
        if (!FabricLoader.getInstance().isModLoaded(getModId()) || lockedValues.isEmpty()) {
            lastApplySuccessful = false;
            return;
        }
        lastApplySuccessful = doApply();
    }

    protected static Object parseValue(Class<?> targetType, String rawValue) {
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(rawValue);
        }
        if (targetType == int.class || targetType == Integer.class) {
            try { return Integer.parseInt(rawValue); } catch (NumberFormatException ignored) { return null; }
        }
        if (targetType == long.class || targetType == Long.class) {
            try { return Long.parseLong(rawValue); } catch (NumberFormatException ignored) { return null; }
        }
        if (targetType == float.class || targetType == Float.class) {
            try { return Float.parseFloat(rawValue); } catch (NumberFormatException ignored) { return null; }
        }
        if (targetType == double.class || targetType == Double.class) {
            try { return Double.parseDouble(rawValue); } catch (NumberFormatException ignored) { return null; }
        }
        if (targetType == String.class) {
            return rawValue;
        }
        return null;
    }

    /** Parse by inferring the wrapper type from an existing value instance. */
    protected static Object parseValueFrom(Object currentValue, String rawValue) {
        if (currentValue == null) return null;
        return parseValue(currentValue.getClass(), rawValue);
    }
}
