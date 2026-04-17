package org.agmas.noellesroles.client.screen;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;

import java.util.UUID;

public final class RoleScreenHelper {
    public static final int MENU_CONTENT_SHIFT_Y = 10;
    public static final int MENU_BUTTON_Y_OFFSET = 42;
    public static final int MENU_BACKGROUND_OVERLAY_COLOR = 0xB0000000;
    public static final int MENU_BAR_HEIGHT = 20;

    private static final int MENU_TITLE_SHIFT_Y = 5;
    private static final int MENU_TITLE_OFFSET_Y = 115;
    private static final int MENU_SUBTITLE_GAP_Y = 15;
    private static final int MENU_VIEW_TOP_OFFSET = 72;
    private static final int MENU_VIEW_BOTTOM_GAP = 4;

    private RoleScreenHelper() {
    }

    public static void drawCenteredTitle(DrawContext context, TextRenderer font, Text text, int x, int y) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(1.5f, 1.5f, 1.5f);
        context.drawCenteredTextWithShadow(font, text, 0, 0, 0xFFFFFF);
        context.getMatrices().pop();
    }

    public static void drawCenteredSubTitle(DrawContext context, TextRenderer font, Text text, int x, int y) {
        context.drawCenteredTextWithShadow(font, text, x, y, 0xAAAAAA);
    }

    public static void renderRoleMenuBackground(DrawContext context, int width, int height, int accentColor) {
        context.fill(0, 0, width, height, MENU_BACKGROUND_OVERLAY_COLOR);
        context.fill(0, 0, width, MENU_BAR_HEIGHT, accentColor);
        context.fill(0, height - MENU_BAR_HEIGHT, width, height, accentColor);
    }

    public static int getGridStartX(int itemCount, int columns, int spacingX, int centerX) {
        return centerX - ((Math.min(itemCount, columns) * spacingX) / 2) + 9;
    }

    public static int getGridRowCount(int itemCount, int columns) {
        return (itemCount + columns - 1) / columns;
    }

    public static int getMenuTitleY(int centerY) {
        return centerY - MENU_TITLE_OFFSET_Y - MENU_TITLE_SHIFT_Y;
    }

    public static int getMenuSubtitleY(int centerY) {
        return getMenuTitleY(centerY) + MENU_SUBTITLE_GAP_Y;
    }

    public static int getMenuStatusY(int centerY) {
        return getMenuSubtitleY(centerY) + MENU_SUBTITLE_GAP_Y;
    }

    public static int getMenuViewTop(int height) {
        return height / 2 - MENU_VIEW_TOP_OFFSET - MENU_TITLE_SHIFT_Y;
    }

    public static int getMenuViewBottom(int height) {
        return height - MENU_BUTTON_Y_OFFSET - MENU_VIEW_BOTTOM_GAP;
    }

    public static int getMenuButtonY(int height) {
        return height - MENU_BUTTON_Y_OFFSET;
    }

    public static boolean intersectsRect(int x, int y, int width, int height, int left, int top, int right, int bottom) {
        return x < right && x + width > left && y < bottom && y + height > top;
    }

    public static boolean containsPoint(double x, double y, int left, int top, int right, int bottom) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    public static void drawSlotHighlight(DrawContext context, int x, int y, int z, int color) {
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, z);
    }

    public static Text getPlayerName(UUID targetUuid, Text fallbackText) {
        PlayerListEntry entry = WatheClient.PLAYER_ENTRIES_CACHE.get(targetUuid);
        if (entry != null && entry.getDisplayName() != null) {
            return entry.getDisplayName();
        }
        return entry != null ? Text.literal(entry.getProfile().getName()) : fallbackText;
    }

    public static SkinTextures getPlayerSkinTextures(UUID targetUuid) {
        PlayerListEntry entry = WatheClient.PLAYER_ENTRIES_CACHE.get(targetUuid);
        if (entry != null) {
            return entry.getSkinTextures();
        }
        return DefaultSkinHelper.getSkinTextures(new GameProfile(targetUuid, "Unknown"));
    }

    public static void renderTopmostPlayerOverlays(DrawContext context, TextRenderer font, Iterable<? extends Element> children) {
        for (Element child : children) {
            if (child instanceof TopmostPlayerOverlayRenderable overlay && overlay.shouldRenderTopmostPlayerOverlay()) {
                overlay.renderTopmostPlayerOverlay(context, font);
            }
        }
    }

    public interface TopmostPlayerOverlayRenderable {
        boolean shouldRenderTopmostPlayerOverlay();

        void renderTopmostPlayerOverlay(DrawContext context, TextRenderer textRenderer);
    }
}
