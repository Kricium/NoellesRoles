package org.agmas.noellesroles.client.mixin.deatharena;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.LobbyPlayersRenderer")
public abstract class LobbyPlayersRendererDeathArenaMixin {
    @ModifyExpressionValue(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;",
                    ordinal = 1
            )
    )
    private static MutableText noellesroles$replaceDeathArenaGamemodeLabel(MutableText original) {
        if (NoellesrolesClient.isDeathArenaActiveForClientPlayer()) {
            return Text.translatable("gamemode.noellesroles.death_arena");
        }
        return original;
    }
}
