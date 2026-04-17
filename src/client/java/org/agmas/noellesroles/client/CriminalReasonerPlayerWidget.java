package org.agmas.noellesroles.client;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public class CriminalReasonerPlayerWidget extends ButtonWidget {
    private final UUID targetUuid;
    private final int clipLeft;
    private final int clipTop;
    private final int clipRight;
    private final int clipBottom;

    public CriminalReasonerPlayerWidget(int x, int y, @NotNull UUID targetUuid, Consumer<UUID> onSelected,
                                        int clipLeft, int clipTop, int clipRight, int clipBottom) {
        super(x, y, 16, 16, getNameText(targetUuid), button -> onSelected.accept(targetUuid), DEFAULT_NARRATION_SUPPLIER);
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
            PlayerSkinDrawer.draw(context, getSkinTextures().texture(), this.getX(), this.getY(), 16);

            if (this.isHovered()) {
                Text name = getNameText(targetUuid);
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
        // 玩家名称只在悬停时显示，避免常驻文本遮挡头像。
    }

    private static Text getNameText(UUID targetUuid) {
        PlayerListEntry entry = WatheClient.PLAYER_ENTRIES_CACHE.get(targetUuid);
        if (entry != null && entry.getDisplayName() != null) {
            return entry.getDisplayName();
        }
        return entry != null ? Text.literal(entry.getProfile().getName()) : Text.literal("Unknown");
    }

    private SkinTextures getSkinTextures() {
        PlayerListEntry entry = WatheClient.PLAYER_ENTRIES_CACHE.get(targetUuid);
        if (entry != null) {
            return entry.getSkinTextures();
        }
        return DefaultSkinHelper.getSkinTextures(new GameProfile(targetUuid, "Unknown"));
    }

}
