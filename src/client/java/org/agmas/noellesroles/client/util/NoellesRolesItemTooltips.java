package org.agmas.noellesroles.client.util;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.mixin.accessor.ItemCooldownEntryAccessor;
import org.agmas.noellesroles.mixin.accessor.ItemCooldownManagerAccessor;

import java.util.List;

public final class NoellesRolesItemTooltips {
    private static final int COOLDOWN_COLOR = 0xC90000;
    private static final Item[] COOLDOWN_TOOLTIP_ITEMS = {
            ModItems.ANTIDOTE,
            ModItems.IRON_MAN_VIAL,
            ModItems.POISON_NEEDLE,
            ModItems.DOUBLE_BARREL_SHOTGUN,
            ModItems.REPAIR_TOOL,
            ModItems.RIOT_SHIELD,
            ModItems.RIOT_FORK,
            ModItems.TIMED_BOMB,
            ModItems.THROWING_AXE,
            ModItems.NEUTRAL_MASTER_KEY
    };

    private NoellesRolesItemTooltips() {
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            for (Item item : COOLDOWN_TOOLTIP_ITEMS) {
                addCooldownTooltip(item, stack, lines);
            }
        });
    }

    private static void addCooldownTooltip(Item item, ItemStack stack, List<Text> lines) {
        if (!stack.isOf(item)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        ItemCooldownManager cooldownManager = player.getItemCooldownManager();
        if (!cooldownManager.isCoolingDown(item)) {
            return;
        }

        ItemCooldownManagerAccessor accessor = (ItemCooldownManagerAccessor) cooldownManager;
        Object entry = accessor.getEntries().get(item);
        if (!(entry instanceof ItemCooldownEntryAccessor cooldownEntry)) {
            return;
        }

        int remainingTicks = cooldownEntry.getEndTick() - accessor.getTick();
        if (remainingTicks <= 0) {
            return;
        }

        int minutes = remainingTicks / 1200;
        int seconds = (remainingTicks - minutes * 1200) / 20;
        String minuteText = minutes > 0 ? minutes + "m" : "";
        String secondText = seconds > 0 ? seconds + "s" : "";
        lines.add(Text.translatable("tip.cooldown", minuteText + secondText).withColor(COOLDOWN_COLOR));
    }
}
