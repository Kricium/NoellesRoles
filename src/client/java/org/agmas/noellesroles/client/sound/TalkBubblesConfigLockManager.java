package org.agmas.noellesroles.client.sound;

import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.ConfigWorldComponent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public final class TalkBubblesConfigLockManager extends AbstractConfigLockManager {
    private static final String MOD_ID = "talkbubbles";
    private static final String AUTO_CONFIG_CLASS = "me.shedaniel.autoconfig.AutoConfig";
    private static final String CONFIG_CLASS = "net.talkbubbles.config.TalkBubblesConfig";
    private static final String MAIN_CLASS = "net.talkbubbles.TalkBubbles";

    private static final TalkBubblesConfigLockManager INSTANCE = new TalkBubblesConfigLockManager();

    private TalkBubblesConfigLockManager() {
    }

    public static void updateFromWorld(ConfigWorldComponent config) { INSTANCE.doUpdateFromWorld(config); }
    public static void tick(MinecraftClient client) { INSTANCE.doTick(client); }
    public static void deactivate() { INSTANCE.doDeactivate(); }

    @Override
    protected String getModId() { return MOD_ID; }

    @Override
    protected boolean isActive(ConfigWorldComponent config) { return config.lockTalkBubblesConfig; }

    @Override
    protected Map<String, String> getLockedValues(ConfigWorldComponent config) { return config.talkBubblesLockedValues; }

    @Override
    protected boolean doApply() {
        try {
            Class<?> autoConfigClass = Class.forName(AUTO_CONFIG_CLASS);
            Class<?> configClass = Class.forName(CONFIG_CLASS);
            Object holder = autoConfigClass.getMethod("getConfigHolder", Class.class).invoke(null, configClass);
            Object config = holder.getClass().getMethod("getConfig").invoke(holder);
            if (config == null) {
                return false;
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

            String newFingerprint = fingerprintBuilder.toString();
            if (changed) {
                holder.getClass().getMethod("save").invoke(holder);
                refreshStaticConfig(config);
            } else if (!newFingerprint.equals(lastFingerprint)) {
                refreshStaticConfig(config);
            }

            lastFingerprint = newFingerprint;
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            if (lastApplySuccessful) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private static void refreshStaticConfig(Object config) throws ReflectiveOperationException {
        Class<?> mainClass = Class.forName(MAIN_CLASS);
        Field configField = mainClass.getField("CONFIG");
        configField.set(null, config);
    }
}
