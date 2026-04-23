package org.agmas.noellesroles.client.mixin.morphling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationVisualHelper;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RoleNameRenderer.class)
public abstract class MorphlingRoleNameRendererMixin {

    @WrapOperation(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
    private static Text noellesroles$swapRoleHudName(PlayerEntity instance, Operation<Text> original) {
        if (instance instanceof AbstractClientPlayerEntity clientPlayer
                && ClientHallucinationVisualHelper.shouldUseShuffledAppearance(clientPlayer)) {
            return Text.literal("??!?!").formatted(Formatting.OBFUSCATED);
        }
        if (instance.isInvisible()) {
            return Text.literal("");
        }
        MorphlingPlayerComponent morphComp = MorphlingPlayerComponent.KEY.get(instance);
        if (morphComp.corpseMode) {
            return Text.literal("");
        }
        if (morphComp.getMorphTicks() > 0) {
            PlayerEntity disguisePlayer = instance.getWorld().getPlayerByUuid(morphComp.disguise);
            if (disguisePlayer != null) {
                return disguisePlayer.getDisplayName();
            }
            PlayerListEntry cachedEntry = WatheClient.PLAYER_ENTRIES_CACHE.get(morphComp.disguise);
            if (cachedEntry != null) {
                return cachedEntry.getDisplayName() != null
                        ? cachedEntry.getDisplayName()
                        : Text.literal(cachedEntry.getProfile().getName());
            }
            if (MinecraftClient.getInstance().player != null
                    && morphComp.disguise.equals(MinecraftClient.getInstance().player.getUuid())) {
                return MinecraftClient.getInstance().player.getDisplayName();
            }
        }
        return original.call(instance);
    }
}
