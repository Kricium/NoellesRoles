package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.hallucination.HallucinationActiveEntry;
import org.agmas.noellesroles.hallucination.HallucinationPlayerComponent;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;
import org.agmas.noellesroles.murdermayhem.MurderMayhemWorldComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class FogOfWarDebugHudMixin {
    @Shadow
    public abstract net.minecraft.client.font.TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void noellesroles$renderFogDebug(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        MurderMayhemWorldComponent component = MurderMayhemWorldComponent.KEY.get(player.getWorld());
        if (!FogOfWarClientHelper.isFogOfWarActive(player.getWorld()) || FogOfWarClientHelper.ignoresFog(player)) {
            return;
        }

        ConfigWorldComponent config = ConfigWorldComponent.KEY.get(player.getWorld());
        HallucinationPlayerComponent hallucinationComponent = HallucinationPlayerComponent.KEY.get(player);

        int x = 6;
        int y = 34;
        if (config.showFogRadiusHud) {
            String line = "迷雾半径: " + component.getFogRadius();
            context.drawTextWithShadow(getTextRenderer(), Text.literal(line), x, y, 0xFFE0E0E0);
            y += 11;
        }

        if (config.showHallucinationHud) {
            context.drawTextWithShadow(getTextRenderer(), Text.literal("幻觉效果"), x, y, 0xFFF4D58D);
            y += 11;
            if (hallucinationComponent.getActiveEntries().isEmpty()) {
                context.drawTextWithShadow(getTextRenderer(), Text.literal("- 无"), x, y, 0xFFB8B8B8);
            } else {
                for (HallucinationActiveEntry entry : hallucinationComponent.getActiveEntries()) {
                    context.drawTextWithShadow(getTextRenderer(), Text.literal("- " + entry.effectId().id()), x, y, 0xFFE0E0E0);
                    y += 10;
                }
            }
        }
    }
}
