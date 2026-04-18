package org.agmas.noellesroles.client.widget;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.agmas.noellesroles.client.screen.RoleScreenHelper.InteractionBlocker;
import org.agmas.noellesroles.client.screen.RoleScreenHelper.TopmostPlayerOverlayRenderable;

public abstract class MenuPlayerTargetWidget extends ButtonWidget implements TopmostPlayerOverlayRenderable {
    protected final int clipLeft;
    protected final int clipTop;
    protected final int clipRight;
    protected final int clipBottom;
    protected final InteractionBlocker[] blockers;

    protected MenuPlayerTargetWidget(int x, int y, Text message, PressAction onPress,
                                     int clipLeft, int clipTop, int clipRight, int clipBottom,
                                     InteractionBlocker... blockers) {
        super(x, y, 16, 16, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.clipLeft = clipLeft;
        this.clipTop = clipTop;
        this.clipRight = clipRight;
        this.clipBottom = clipBottom;
        this.blockers = blockers;
    }

    protected abstract SkinTextures getSkinTextures();

    protected abstract ShopEntry.Type getBackgroundType();

    protected abstract Text getOverlayText();

    protected int getHighlightColor() {
        return 0x913D3D3D;
    }

    protected boolean shouldHighlight() {
        return this.isHovered();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(clipLeft, clipTop, clipRight, clipBottom);
        try {
            this.hovered = RoleScreenHelper.isPointWithinPlayerWidgetFrame(
                    mouseX, mouseY, this.getX(), this.getY(), clipLeft, clipTop, clipRight, clipBottom, blockers);
            super.renderWidget(context, mouseX, mouseY, delta);
            context.drawGuiTexture(getBackgroundType().getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (shouldHighlight()) {
                RoleScreenHelper.drawSlotHighlight(context, this.getX(), this.getY(), 0, getHighlightColor());
            }
        } finally {
            context.disableScissor();
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return RoleScreenHelper.isPointWithinPlayerWidgetFrame(
                mouseX, mouseY, this.getX(), this.getY(), clipLeft, clipTop, clipRight, clipBottom, blockers);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && this.isMouseOver(mouseX, mouseY);
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
        Text text = getOverlayText();
        context.drawTooltip(textRenderer, text, this.getX() - 4 - textRenderer.getWidth(text) / 2, this.getY() - 9);
    }
}
