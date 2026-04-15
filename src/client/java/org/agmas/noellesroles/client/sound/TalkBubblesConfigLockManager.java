package org.agmas.noellesroles.client.sound;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.ConfigWorldComponent;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class TalkBubblesConfigLockManager {
    private static final String MOD_ID = "talkbubbles";
    private static final String AUTO_CONFIG_CLASS = "me.shedaniel.autoconfig.AutoConfig";
    private static final String CONFIG_CLASS = "net.talkbubbles.config.TalkBubblesConfig";
    private static final String MAIN_CLASS = "net.talkbubbles.TalkBubbles";

    private static boolean active;
    private static final Map<String, String> lockedValues = new LinkedHashMap<>();
    private static boolean lastApplySuccessful;
    private static String lastFingerprint = "";

    private TalkBubblesConfigLockManager() {
    }

    public static void updateFromWorld(ConfigWorldComponent config) {
        if (config == null || !config.lockTalkBubblesConfig) {
            deactivate();
            return;
        }
        active = true;
        lockedValues.clear();
        lockedValues.putAll(config.talkBubblesLockedValues);
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
            Class<?> autoConfigClass = Class.forName(AUTO_CONFIG_CLASS);
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Object holder = autoConfigClass.getMethod("getConfigHolder", Class.class).invoke(null, configClass);
            Object config = holder.getClass().getMethod("getConfig").invoke(holder);
            if (config == null) {
                lastApplySuccessful = false;
                return;
            }

            boolean changed = false;
            StringBuilder fingerprintBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : lockedValues.entrySet()) {
                Field field;
                try {
                    field = configClass.getField(entry.getKey());
                } catch (NoSuchFieldException ignored) {
                    continue;
                }

                Object currentValue = field.get(config);
                Object parsedValue = parseValue(field.getType(), entry.getValue());
                if (parsedValue == null) {
                    continue;
                }

                fingerprintBuilder.append(entry.getKey()).append('=').append(parsedValue).append(';');
                if (!Objects.equals(currentValue, parsedValue)) {
                    field.set(config, parsedValue);
                    changed = true;
                }
            }

            if (changed) {
                holder.getClass().getMethod("save").invoke(holder);
                refreshStaticConfig(config);
            } else if (!fingerprintBuilder.toString().equals(lastFingerprint)) {
                refreshStaticConfig(config);
            }

            lastFingerprint = fingerprintBuilder.toString();
            lastApplySuccessful = true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            if (lastApplySuccessful) {
                e.printStackTrace();
            }
            lastApplySuccessful = false;
        }
    }

    private static void refreshStaticConfig(Object config) throws ReflectiveOperationException {
        Class<?> mainClass = Class.forName(MAIN_CLASS);
        Field configField = mainClass.getField("CONFIG");
        configField.set(null, config);
    }

    private static Object parseValue(Class<?> targetType, String rawValue) {
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(rawValue);
        }
        if (targetType == int.class || targetType == Integer.class) {
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (targetType == long.class || targetType == Long.class) {
            try {
                return Long.parseLong(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (targetType == float.class || targetType == Float.class) {
            try {
                return Float.parseFloat(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (targetType == double.class || targetType == Double.class) {
            try {
                return Double.parseDouble(rawValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (targetType == String.class) {
            return rawValue;
        }
        return null;
    }
}
