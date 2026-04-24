package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.api.event.CanSeeMoney;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.gui.MoodRenderer;
import dev.doctor4t.wathe.client.gui.StoreRenderer;
import dev.doctor4t.wathe.client.gui.TimeRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.hallucination.HallucinationEffectId;
import org.agmas.noellesroles.hallucination.HallucinationPlayerComponent;
import org.agmas.noellesroles.hallucination.HallucinationUiSlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class HallucinationHudRenderer {
    private static boolean renderingFakeSanity;

    private HallucinationHudRenderer() {
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || !GameFunctions.isPlayerPlayingAndAlive(player)) {
            return;
        }

        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        TextRenderer renderer = client.textRenderer;
        float delta = tickCounter.getTickDelta(true);

        if (component.hasEffect(HallucinationEffectId.FAKE_TIME)
                && canRenderFakeTime(player)
                && !component.isUiHidden(HallucinationUiSlot.TIME)) {
            renderFakeTime(renderer, player, context, delta);
        }
        if (component.hasEffect(HallucinationEffectId.FAKE_MONEY)
                && canRenderFakeMoney(player)
                && !component.isUiHidden(HallucinationUiSlot.MONEY)) {
            renderFakeMoney(renderer, player, context, delta);
        }
        if (component.hasEffect(HallucinationEffectId.FAKE_SANITY)
                && !component.isUiHidden(HallucinationUiSlot.SANITY)) {
            renderFakeSanity(renderer, player, context, tickCounter);
        }
    }

    public static boolean shouldSuppressTimeHud() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        return component.isUiHidden(HallucinationUiSlot.TIME)
                || (component.hasEffect(HallucinationEffectId.FAKE_TIME) && canRenderFakeTime(player));
    }

    public static boolean shouldSuppressMoneyHud() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        return component.isUiHidden(HallucinationUiSlot.MONEY)
                || (component.hasEffect(HallucinationEffectId.FAKE_MONEY) && canRenderFakeMoney(player));
    }

    public static boolean shouldSuppressSanityHud() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        return component.isUiHidden(HallucinationUiSlot.SANITY)
                || (component.hasEffect(HallucinationEffectId.FAKE_SANITY) && !renderingFakeSanity);
    }

    public static boolean shouldSuppressSkillHintHud() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(player).isUiHidden(HallucinationUiSlot.SKILL_HINT);
    }

    private static void renderFakeTime(TextRenderer renderer, @NotNull ClientPlayerEntity player, DrawContext context, float delta) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        int time = GameTimeComponent.KEY.get(player.getWorld()).getTime() + (component.getFakeTimeOffsetSeconds() * 20);
        if (Math.abs(TimeRenderer.view.getTarget() - time) > 10) {
            TimeRenderer.offsetDelta = time > TimeRenderer.view.getTarget() ? 0.6F : -0.6F;
        }
        if (time < dev.doctor4t.wathe.game.GameConstants.getInTicks(1, 0)) {
            TimeRenderer.offsetDelta = -0.9F;
        } else {
            TimeRenderer.offsetDelta = MathHelper.lerp(delta / 16.0F, TimeRenderer.offsetDelta, 0.0F);
        }
        TimeRenderer.view.setTarget(time);

        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth() / 2.0F, 6.0F, 0.0F);
        TimeRenderer.view.render(renderer, context, 0, 0, 0xFFFFFFFF, delta);
        context.getMatrices().pop();
    }

    private static void renderFakeMoney(TextRenderer renderer, @NotNull ClientPlayerEntity player, DrawContext context, float delta) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        int balance = PlayerShopComponent.KEY.get(player).getBalance() + component.getFakeMoneyAmount();
        if (StoreRenderer.view.getTarget() != balance) {
            StoreRenderer.offsetDelta = balance > StoreRenderer.view.getTarget() ? 0.6F : -0.6F;
            StoreRenderer.view.setTarget(balance);
        }

        float r = StoreRenderer.offsetDelta > 0.0F ? 1.0F - StoreRenderer.offsetDelta : 1.0F;
        float g = StoreRenderer.offsetDelta < 0.0F ? 1.0F + StoreRenderer.offsetDelta : 1.0F;
        float b = 1.0F - Math.abs(StoreRenderer.offsetDelta);
        int colour = MathHelper.packRgb(r, g, b) | 0xFF000000;

        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth() - 12.0F, 6.0F, 0.0F);
        StoreRenderer.view.render(renderer, context, 0, 0, colour, delta);
        context.getMatrices().pop();
        StoreRenderer.offsetDelta = MathHelper.lerp(delta / 16.0F, StoreRenderer.offsetDelta, 0.0F);
    }

    private static void renderFakeSanity(TextRenderer renderer,
                                         @NotNull ClientPlayerEntity player,
                                         DrawContext context,
                                         RenderTickCounter tickCounter) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
        float originalMood = mood.getMood();
        Map<PlayerMoodComponent.Task, PlayerMoodComponent.TrainTask> originalTasks = new EnumMap<>(PlayerMoodComponent.Task.class);
        originalTasks.putAll(mood.tasks);

        float fakeMood = MathHelper.clamp(component.getFakeSanityPercent(), 0, 100) / 100.0F;
        int taskCount = Math.max(0, component.getFakeTaskCount());

        mood.setMood(fakeMood);
        mood.tasks.clear();
        List<PlayerMoodComponent.Task> orderedTasks = new ArrayList<>(List.of(PlayerMoodComponent.Task.values()));
        for (int i = 0; i < taskCount && i < orderedTasks.size(); i++) {
            PlayerMoodComponent.Task task = orderedTasks.get(i);
            mood.tasks.put(task, createFakeTask(task));
        }

        renderingFakeSanity = true;
        try {
            MoodRenderer.renderHud(player, renderer, context, tickCounter);
        } finally {
            renderingFakeSanity = false;
            mood.tasks.clear();
            mood.tasks.putAll(originalTasks);
            mood.setMood(originalMood);
        }
    }

    private static PlayerMoodComponent.TrainTask createFakeTask(PlayerMoodComponent.Task task) {
        return switch (task) {
            case SLEEP -> new PlayerMoodComponent.SleepTask(1);
            case OUTSIDE -> new PlayerMoodComponent.OutsideTask(1);
            case EAT -> new PlayerMoodComponent.EatTask();
            case DRINK -> new PlayerMoodComponent.DrinkTask();
        };
    }

    private static boolean canRenderFakeTime(@NotNull ClientPlayerEntity player) {
        return GameWorldComponent.KEY.get(player.getWorld()).canUseKillerFeatures(player);
    }

    private static boolean canRenderFakeMoney(@NotNull ClientPlayerEntity player) {
        return CanSeeMoney.EVENT.invoker().canSee(player) == CanSeeMoney.Result.ALLOW;
    }
}
