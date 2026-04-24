package org.agmas.noellesroles.client.mixin.roundend;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RoundTextRenderer.class)
public class RoundTextRendererSkinCacheMixin {

    @WrapOperation(
            method = "getPlayerTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/PlayerSkinProvider;getSkinTextures(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/util/SkinTextures;"
            )
    )
    private static SkinTextures noellesroles$avoidRoundEndSkinFetch(
            PlayerSkinProvider provider,
            GameProfile profile,
            Operation<SkinTextures> original
    ) {
        if (profile == null || profile.getId() == null) {
            return DefaultSkinHelper.getSkinTextures(profile);
        }

        if (!WatheClient.PLAYER_ENTRIES_CACHE.containsKey(profile.getId())) {
            return DefaultSkinHelper.getSkinTextures(profile);
        }

        return original.call(provider, profile);
    }
}
