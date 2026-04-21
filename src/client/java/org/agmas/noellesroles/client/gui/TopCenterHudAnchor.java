package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.client.gui.HudHeaderLayout;

public final class TopCenterHudAnchor {
    private static int nextTopY = 6;

    private TopCenterHudAnchor() {
    }

    public static void beginFrame() {
        nextTopY = 6;
    }

    public static void includeLayout(HudHeaderLayout layout) {
        if (layout == null) {
            return;
        }
        nextTopY = Math.max(nextTopY, layout.matchCountBottomY() + 2);
        nextTopY = Math.max(nextTopY, layout.broadcastTopY() + 12);
    }

    public static void includeHeight(int bottomY) {
        nextTopY = Math.max(nextTopY, bottomY + 2);
    }

    public static int getTopY() {
        return nextTopY;
    }
}
