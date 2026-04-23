package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.agmas.noellesroles.ModEffects;

import java.util.List;

public class MedicalSplintItem extends Item {
    private static final int MAX_USE_TIME = 72000;
    private static final int BONE_SETTING_INTERVAL_TICKS = 2 * 20;
    private static final int BONE_SETTING_DURATION_TICKS = 20;

    public MedicalSplintItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.medical_splint.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!GameFunctions.isPlayerAliveAndSurvival(user)) {
            return TypedActionResult.pass(stack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        stopHorizontalMovement(user);
        if (world.isClient || !(user instanceof ServerPlayerEntity player)) {
            return;
        }

        int elapsedUseTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (elapsedUseTicks > 0 && elapsedUseTicks % BONE_SETTING_INTERVAL_TICKS == 0) {
            player.addStatusEffect(new StatusEffectInstance(ModEffects.BONE_SETTING, BONE_SETTING_DURATION_TICKS, 0, false, true, true));
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            GameRecordManager.recordItemUse(serverPlayer, Registries.ITEM.getId(this), null, null);
        }

        stack.decrementUnlessCreative(1, player);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    private static void stopHorizontalMovement(LivingEntity user) {
        Vec3d velocity = user.getVelocity();
        user.setVelocity(0.0D, velocity.y, 0.0D);
        user.velocityModified = true;
    }
}
