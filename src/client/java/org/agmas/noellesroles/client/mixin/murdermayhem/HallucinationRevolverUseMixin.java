package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.doctor4t.wathe.item.RevolverItem")
public class HallucinationRevolverUseMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
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
        Identifier deathReason = GameConstants.DeathReasons.GUN;
        if (!ClientHallucinationState.tryHitDummy(client, 30.0D, deathReason)) {
            return;
        }
        user.setPitch(user.getPitch() - 4.0F);
        cir.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
    }
}
