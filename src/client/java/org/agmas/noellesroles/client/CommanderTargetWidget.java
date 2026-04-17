package org.agmas.noellesroles.client;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.agmas.noellesroles.commander.CommanderPlayerComponent;

import java.util.UUID;
import java.util.function.Consumer;

public class CommanderTargetWidget extends ButtonWidget {
    private static final Text UNKNOWN_PLAYER_TEXT = Text.literal("Unknown");

    private final UUID targetUuid;
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public CommanderTargetWidget(int x, int y, UUID targetUuid, Consumer<UUID> onTargetSelected,
                                 int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 16, 16, RoleScreenHelper.getPlayerName(targetUuid, UNKNOWN_PLAYER_TEXT), button -> onTargetSelected.accept(targetUuid), DEFAULT_NARRATION_SUPPLIER);
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
            CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
            boolean marked = commanderComp.isThreatTarget(this.targetUuid);

            context.drawGuiTexture(ShopEntry.Type.WEAPON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, RoleScreenHelper.getPlayerSkinTextures(this.targetUuid).texture(), this.getX(), this.getY(), 16);

            if (marked || this.isHovered()) {
                RoleScreenHelper.drawSlotHighlight(context, this.getX(), this.getY(), 0, marked ? 0xAA4B1A8E : 0x913D3D3D);
                Text name = RoleScreenHelper.getPlayerName(this.targetUuid, UNKNOWN_PLAYER_TEXT);
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, name,
                        this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(name) / 2,
                        this.getY() - 9);
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
