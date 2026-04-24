package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.TimeRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.looseend.LooseEndsRadarWorldComponent;
import org.jetbrains.annotations.NotNull;

public final class LooseEndsRadarHudRenderer {
    private static final TimeRenderer.TimeNumberRenderer VIEW = new TimeRenderer.TimeNumberRenderer();
    private static float offsetDelta = 0.0F;
    private static final int SCANNING_NUMBER_COLOUR = 0xFFBDF6FF;
    private static final int LABEL_COLOUR = 0xFFE8F7FF;
    private static final int DEFAULT_NUMBER_COLOUR = 0xFFFFFFFF;

    private LooseEndsRadarHudRenderer() {}

    public static void renderHud(TextRenderer renderer, @NotNull ClientPlayerEntity player, @NotNull DrawContext context, float delta) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!shouldRenderFor(gameWorld)) {
            return;
        }

        LooseEndsRadarWorldComponent radarComponent = LooseEndsRadarWorldComponent.KEY.get(player.getWorld());
        if (!radarComponent.shouldRenderHud()) {
            return;
        }

        int time = radarComponent.getHudTimeTicks();
        updateAnimation(time, delta);

        int colour = getNumberColour(radarComponent.isScanning(), time);
        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth() / 2.0F, 6.0F, 0.0F);
        VIEW.render(renderer, context, 0, 0, colour, delta);
        context.getMatrices().pop();

        Text subtitle = Text.translatable(radarComponent.getHudLabelKey());
        int subtitleY = 18;
        context.drawCenteredTextWithShadow(renderer, subtitle, context.getScaledWindowWidth() / 2, subtitleY, LABEL_COLOUR);
        TopCenterHudAnchor.includeHeight(subtitleY + renderer.fontHeight);
    }

    public static boolean shouldReplaceDefaultTimeHud() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return false;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(client.player.getWorld());
        if (!shouldRenderFor(gameWorld)) {
            return false;
        }

        return LooseEndsRadarWorldComponent.KEY.get(client.player.getWorld()).shouldRenderHud();
    }

    public static boolean shouldHideDefaultMatchPlayerCount() {
        return shouldReplaceDefaultTimeHud();
    }

    public static void tick() {
        VIEW.update();
    }

    private static boolean shouldRenderFor(GameWorldComponent gameWorld) {
        return gameWorld.getGameStatus() == GameWorldComponent.GameStatus.ACTIVE
                && (NoellesrolesClient.isDeathArenaActiveForClientPlayer()
                || gameWorld.getGameMode() == dev.doctor4t.wathe.api.WatheGameModes.LOOSE_ENDS);
    }

    private static void updateAnimation(int time, float delta) {
        if (Math.abs(VIEW.getTarget() - time) > 10) {
            offsetDelta = time > VIEW.getTarget() ? 0.6F : -0.6F;
        }

        if (time < 20 * 10) {
            offsetDelta = -0.9F;
        } else {
            offsetDelta = 0.0F;
        }

        VIEW.setTarget(time);
    }

    private static int getNumberColour(boolean scanning, int time) {
        if (scanning) {
            return SCANNING_NUMBER_COLOUR;
        }
        if (time < 20 * 10) {
            float pulse = (float) Math.sin(time / 3.0) * 0.3F + 0.7F;
            return MathHelper.packRgb(1.0F, 0.2F * pulse, 0.4F * pulse) | 0xFF000000;
        }
        return DEFAULT_NUMBER_COLOUR;
    }
}
