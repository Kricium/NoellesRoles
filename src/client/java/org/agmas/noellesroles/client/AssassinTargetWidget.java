package org.agmas.noellesroles.client;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import org.agmas.noellesroles.assassin.AssassinPlayerComponent;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.agmas.noellesroles.client.screen.RoleScreenHelper.InteractionBlocker;
import org.agmas.noellesroles.client.widget.MenuPlayerTargetWidget;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;

public class AssassinTargetWidget extends MenuPlayerTargetWidget {
    private static final Text UNKNOWN_PLAYER_TEXT = Text.literal("Unknown");
    private static final int HIGHLIGHT_COLOR = -1862287543;

    public final UUID targetUuid;

    public AssassinTargetWidget(int x, int y, @NotNull UUID targetUuid,
                                Consumer<UUID> onTargetSelected, int clipLeft, int clipTop, int clipRight, int clipBottom,
                                InteractionBlocker... blockers) {
        super(x, y, RoleScreenHelper.getPlayerName(targetUuid, UNKNOWN_PLAYER_TEXT), button -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }
            AssassinPlayerComponent assassinComp = AssassinPlayerComponent.KEY.get(player);
            if (assassinComp.canGuess()) {
                onTargetSelected.accept(targetUuid);
            }
        }, clipLeft, clipTop, clipRight, clipBottom, blockers);
        this.targetUuid = targetUuid;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            this.hovered = false;
            return;
        }
        AssassinPlayerComponent assassinComp = AssassinPlayerComponent.KEY.get(player);
        if (!assassinComp.canGuess()) {
            context.enableScissor(clipLeft, clipTop, clipRight, clipBottom);
            try {
                this.hovered = RoleScreenHelper.isPointWithinPlayerWidgetFrame(
                        mouseX, mouseY, this.getX(), this.getY(), clipLeft, clipTop, clipRight, clipBottom, blockers);
                context.setShaderColor(0.25f, 0.25f, 0.25f, 0.5f);
                context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
                PlayerSkinDrawer.draw(context, RoleScreenHelper.getPlayerSkinTextures(this.targetUuid).texture(), this.getX(), this.getY(), 16);
                context.setShaderColor(1f, 1f, 1f, 1f);
                if (assassinComp.getCooldownTicks() > 0) {
                    String cooldown = String.valueOf(assassinComp.getCooldownTicks() / 20);
                    context.drawText(MinecraftClient.getInstance().textRenderer, cooldown,
                            this.getX(), this.getY(), Color.RED.getRGB(), true);
                }
                if (this.isHovered()) {
                    RoleScreenHelper.drawSlotHighlight(context, this.getX(), this.getY(), 0, HIGHLIGHT_COLOR);
                }
            } finally {
                context.disableScissor();
            }
            return;
        }
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected SkinTextures getSkinTextures() {
        return RoleScreenHelper.getPlayerSkinTextures(this.targetUuid);
    }

    @Override
    protected ShopEntry.Type getBackgroundType() {
        return ShopEntry.Type.POISON;
    }

    @Override
    protected Text getOverlayText() {
        return RoleScreenHelper.getPlayerName(this.targetUuid, UNKNOWN_PLAYER_TEXT);
    }

    @Override
    protected int getHighlightColor() {
        return HIGHLIGHT_COLOR;
    }

    @Override
    public boolean shouldRenderTopmostPlayerOverlay() {
        return this.visible && this.isHovered();
    }
}
