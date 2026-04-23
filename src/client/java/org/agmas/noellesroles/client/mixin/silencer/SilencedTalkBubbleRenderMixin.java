package org.agmas.noellesroles.client.mixin.silencer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.agmas.noellesroles.client.silencer.TalkBubbleRenderContext;
import org.agmas.noellesroles.silencer.SilencedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.talkbubbles.util.RenderBubble")
public class SilencedTalkBubbleRenderMixin {

    @Inject(
            method = "renderBubble(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Ljava/util/List;IIFI)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void noellesroles$hideOtherTalkBubblesWhenSilenced(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        AbstractClientPlayerEntity renderedPlayer = TalkBubbleRenderContext.getCurrentPlayer();
        if (client.player == null || renderedPlayer == null) {
            return;
        }

        if (SilencedPlayerComponent.isPlayerSilenced(renderedPlayer)) {
            ci.cancel();
            return;
        }

        if (HallucinationClientVisibilityHelper.shouldHidePlayer(client.player, renderedPlayer)) {
            ci.cancel();
            return;
        }

        if (SilencedPlayerComponent.isPlayerSilenced(client.player)
                && !renderedPlayer.getUuid().equals(client.player.getUuid())) {
            ci.cancel();
        }
    }
}
