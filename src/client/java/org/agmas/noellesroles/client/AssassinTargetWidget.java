package org.agmas.noellesroles.client;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.assassin.AssassinPlayerComponent;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.agmas.noellesroles.client.screen.RoleScreenHelper.TopmostPlayerOverlayRenderable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;

public class AssassinTargetWidget extends ButtonWidget implements TopmostPlayerOverlayRenderable {
    private static final Text UNKNOWN_PLAYER_TEXT = Text.literal("Unknown");
    private static final int HIGHLIGHT_COLOR = -1862287543;

    public final UUID targetUuid;
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public AssassinTargetWidget(int x, int y, @NotNull UUID targetUuid,
                                Consumer<UUID> onTargetSelected, int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 16, 16, RoleScreenHelper.getPlayerName(targetUuid, UNKNOWN_PLAYER_TEXT), button -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }
            AssassinPlayerComponent assassinComp = AssassinPlayerComponent.KEY.get(player);
            if (assassinComp.canGuess()) {
                onTargetSelected.accept(targetUuid);
            }
        }, DEFAULT_NARRATION_SUPPLIER);
        this.targetUuid = targetUuid;
        this.clipLeft = clipLeft;
        this.clipTop = clipTop;
        this.clipRight = clipRight;
        this.clipBottom = clipBottom;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(clipLeft, clipTop, clipRight, clipBottom);
        try {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }
            super.renderWidget(context, mouseX, mouseY, delta);
            AssassinPlayerComponent assassinComp = AssassinPlayerComponent.KEY.get(player);

            if (!assassinComp.canGuess()) {
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
                return;
            }

            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, RoleScreenHelper.getPlayerSkinTextures(this.targetUuid).texture(), this.getX(), this.getY(), 16);

            if (this.isHovered()) {
                RoleScreenHelper.drawSlotHighlight(context, this.getX(), this.getY(), 0, HIGHLIGHT_COLOR);
            }
        } finally {
            context.disableScissor();
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return RoleScreenHelper.containsPoint(mouseX, mouseY, clipLeft, clipTop, clipRight, clipBottom)
                && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
    }

    @Override
    public boolean shouldRenderTopmostPlayerOverlay() {
        return this.visible && this.isHovered();
    }

    @Override
    public void renderTopmostPlayerOverlay(DrawContext context, TextRenderer textRenderer) {
        context.drawTooltip(textRenderer, this.getTooltipText(), this.getTooltipX(textRenderer), this.getTooltipY());
    }

    public Text getTooltipText() {
        return RoleScreenHelper.getPlayerName(this.targetUuid, UNKNOWN_PLAYER_TEXT);
    }

    public int getTooltipX(TextRenderer textRenderer) {
        Text tooltip = this.getTooltipText();
        return this.getX() - 4 - textRenderer.getWidth(tooltip) / 2;
    }

    public int getTooltipY() {
        return this.getY() - 9;
    }
}
