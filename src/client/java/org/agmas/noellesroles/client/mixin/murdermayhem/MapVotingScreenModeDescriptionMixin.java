package org.agmas.noellesroles.client.mixin.murdermayhem;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.doctor4t.wathe.cca.MapVotingComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.screen.MapVotingScreen")
public abstract class MapVotingScreenModeDescriptionMixin {
    @ModifyExpressionValue(
            method = "drawModeTicketCard",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/MapVotingComponent$VotingModeEntry;description()Ljava/lang/String;"
            )
    )
    private String noellesroles$translateModeDescription(
            String original,
            DrawContext context,
            int x,
            int y,
            MapVotingComponent.VotingModeEntry entry,
            boolean selected,
            boolean hovered,
            boolean unavailable,
            int votes,
            int totalVotes,
            float hoverScale
    ) {
        if (original == null || original.isBlank()) {
            return original;
        }
        if (Language.getInstance().hasTranslation(original)) {
            return Text.translatable(original).getString();
        }
        return original;
    }
}
