package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.doctor4t.wathe.item.KnifeItem")
public class HallucinationKnifeUseMixin {

    @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true, remap = false)
    private void noellesroles$stabHallucinationDummy(
            ItemStack stack,
            World world,
            LivingEntity user,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!world.isClient || !(user instanceof net.minecraft.entity.player.PlayerEntity)) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != user) {
            return;
        }
        Identifier deathReason = GameConstants.DeathReasons.KNIFE;
        if (ClientHallucinationState.tryHitDummy(client, 3.0D, deathReason)) {
            ci.cancel();
        }
    }
}
