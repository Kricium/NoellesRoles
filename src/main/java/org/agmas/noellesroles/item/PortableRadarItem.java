package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.looseend.LooseEndsRadarHelper;

import java.util.List;

public class PortableRadarItem extends Item {
    public PortableRadarItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.portable_radar.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof ServerPlayerEntity serverPlayer)) {
            return TypedActionResult.pass(stack);
        }

        if (LooseEndsRadarHelper.isActiveLooseEnds(world) && !LooseEndsRadarHelper.tryStartManualScan(serverWorld)) {
            serverPlayer.sendMessage(Text.translatable("item.noellesroles.portable_radar.busy"), true);
            return TypedActionResult.fail(stack);
        }

        LooseEndsRadarHelper.applyRadarGlow(serverWorld, user.getUuid());
        GameRecordManager.recordItemUse(serverPlayer, Registries.ITEM.getId(this), null, null);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(stack, false);
    }
}
