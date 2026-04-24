package org.agmas.noellesroles.client.mixin.looseend;

import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.RoleNameRenderer")
public class LooseEndOpeningPhaseRoleNameRendererMixin {

    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true, remap = false)
    private static void noellesroles$hideRoleNameHudForOpeningPhase(CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (player != null && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            ci.cancel();
        }
    }
}
