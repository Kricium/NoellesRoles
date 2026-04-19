package org.agmas.noellesroles.client.mixin.silencer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.silencer.SilencedTalkBubbleCleaner;
import org.agmas.noellesroles.silencer.SilencedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ChatHud.class, priority = 1200)
public abstract class SilencedChatBubbleMessageMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(
            method = "addMessage",
            at = @At("TAIL"),
            require = 0
    )
    private void noellesroles$clearSilencedTalkBubbleAfterMessage(
            Text message,
            CallbackInfo ci
    ) {
        if (client == null || client.world == null || client.player == null) {
            return;
        }

        String senderName = noellesroles$extractSender(message);
        if (!senderName.isEmpty()) {
            UUID senderUuid = client.getNetworkHandler() != null
                    ? client.getNetworkHandler().getPlayerListEntry(senderName) != null
                        ? client.getNetworkHandler().getPlayerListEntry(senderName).getProfile().getId()
                        : null
                    : null;
            if (senderUuid != null) {
                PlayerEntity senderEntity = client.world.getPlayerByUuid(senderUuid);
                if (senderEntity instanceof AbstractClientPlayerEntity senderPlayer
                        && SilencedPlayerComponent.isPlayerSilenced(senderPlayer)) {
                    SilencedTalkBubbleCleaner.clearBubbleReflectively(senderPlayer);
                }
            }
        }

        if (SilencedPlayerComponent.isPlayerSilenced(client.player)) {
            for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                if (player == client.player) {
                    continue;
                }
                SilencedTalkBubbleCleaner.clearBubbleReflectively(player);
            }
        }
    }

    private String noellesroles$extractSender(Text message) {
        String[] splitString = message.getString().split("(Ąė.)|[^\\\\wĄė]+");
        String[] splitKey = message.toString().split("key='");
        if (splitKey.length > 1) {
            String key = splitKey[1].split("'")[0];
            if (key.contains("commands") || key.contains("advancement")) {
                return "";
            }
        }

        for (int i = 0; i < splitString.length; i++) {
            String part = splitString[i];
            if (part.isEmpty()) {
                continue;
            }
            if (client.getNetworkHandler() == null) {
                return "";
            }
            if (client.getNetworkHandler().getPlayerListEntry(part) != null) {
                return part;
            }
        }
        return "";
    }
}
