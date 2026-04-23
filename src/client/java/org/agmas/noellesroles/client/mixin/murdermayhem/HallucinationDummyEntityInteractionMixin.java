package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.agmas.noellesroles.item.RiotShieldItem;
import org.agmas.noellesroles.packet.HallucinationDummyUseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class HallucinationDummyEntityInteractionMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void noellesroles$attackHallucinationDummy(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!ClientHallucinationState.isDummyEntity(target)) {
            return;
        }

        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            return;
        }

        if (stack.isOf(WatheItems.BAT)) {
            if (ClientHallucinationState.tryHitDummyEntity(MinecraftClient.getInstance(), target, GameConstants.DeathReasons.BAT)) {
                ci.cancel();
            }
            return;
        }

        if (stack.isOf(ModItems.RIOT_SHIELD) || ClientHallucinationState.isNonlethalDummyMelee(stack)) {
            if (ClientHallucinationState.tryHurtDummyEntity(MinecraftClient.getInstance(), target)) {
                if (stack.isOf(ModItems.RIOT_SHIELD)) {
                    player.getItemCooldownManager().set(ModItems.RIOT_SHIELD, RiotShieldItem.SHOVE_COOLDOWN_TICKS);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
    private void noellesroles$interactHallucinationDummy(PlayerEntity player,
                                                         Entity entity,
                                                         Hand hand,
                                                         CallbackInfoReturnable<ActionResult> cir) {
        HallucinationDummyUseAction action = ClientHallucinationState.resolveDummyUseAction(player.getStackInHand(hand), player);
        if (action == null || !ClientHallucinationState.tryUseDummyEntity(entity, action)) {
            return;
        }
        player.swingHand(hand);
        cir.setReturnValue(ActionResult.CONSUME);
    }

    @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
    private void noellesroles$interactHallucinationDummyAtLocation(PlayerEntity player,
                                                                   Entity entity,
                                                                   net.minecraft.util.hit.EntityHitResult hitResult,
                                                                   Hand hand,
                                                                   CallbackInfoReturnable<ActionResult> cir) {
        HallucinationDummyUseAction action = ClientHallucinationState.resolveDummyUseAction(player.getStackInHand(hand), player);
        if (action == null || !ClientHallucinationState.tryUseDummyEntity(entity, action)) {
            return;
        }
        player.swingHand(hand);
        cir.setReturnValue(ActionResult.CONSUME);
    }
}
