package org.agmas.noellesroles.client;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public class CriminalReasonerPlayerWidget extends ButtonWidget {
    private static final Text UNKNOWN_PLAYER_TEXT = Text.literal("Unknown");

    private final UUID targetUuid;
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public CriminalReasonerPlayerWidget(int x, int y, @NotNull UUID targetUuid, Consumer<UUID> onSelected,
                                        int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 16, 16, RoleScreenHelper.getPlayerName(targetUuid, UNKNOWN_PLAYER_TEXT), button -> onSelected.accept(targetUuid), DEFAULT_NARRATION_SUPPLIER);
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
            super.renderWidget(context, mouseX, mouseY, delta);

            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, RoleScreenHelper.getPlayerSkinTextures(this.targetUuid).texture(), this.getX(), this.getY(), 16);

            if (this.isHovered()) {
                Text name = RoleScreenHelper.getPlayerName(this.targetUuid, UNKNOWN_PLAYER_TEXT);
                int tooltipX = this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(name) / 2;
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, name, tooltipX, this.getY() - 9);
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
}
