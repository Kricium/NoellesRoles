package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.item.RevolverItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.agmas.noellesroles.item.DoubleBarrelShotgunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoubleBarrelShotgunItem.class)
public class HallucinationDoubleBarrelShotgunMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void noellesroles$shootHallucinationDummy(
            World world,
            PlayerEntity user,
            Hand hand,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir
    ) {
        if (!world.isClient) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != user) {
            return;
        }
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown((DoubleBarrelShotgunItem) (Object) this)
                || DoubleBarrelShotgunItem.getLoadedShells(stack) <= 0) {
            return;
        }
        if (!ClientHallucinationState.tryHitDummy(client, DoubleBarrelShotgunItem.RANGE, GameConstants.DeathReasons.GUN)) {
            return;
        }
        user.setPitch(user.getPitch() - 4.0F);
        RevolverItem.spawnHandParticle();
        cir.setReturnValue(TypedActionResult.consume(stack));
    }
}
