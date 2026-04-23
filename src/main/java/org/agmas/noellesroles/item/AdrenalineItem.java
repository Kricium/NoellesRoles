package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.world.World;
import org.agmas.noellesroles.ModEffects;
import org.agmas.noellesroles.effect.StimulationEffect;

import java.util.List;

public class AdrenalineItem extends Item {
    private static final int USE_TIME = 40;
    private static final int EFFECT_DURATION = 7 * 20;

    public AdrenalineItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.adrenaline.tooltip"));
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
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return stack;
        }

        if (!GameFunctions.isPlayerAliveAndSurvival(player)) {
            return stack;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, EFFECT_DURATION, 1, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(ModEffects.STIMULATION, EFFECT_DURATION, 0, false, false, true));
        StimulationEffect.applyStaminaModifier(player);
        GameRecordManager.recordItemUse(player, Registries.ITEM.getId(this), null, null);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.decrementUnlessCreative(1, player);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }
}
