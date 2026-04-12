package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

@FunctionalInterface
public interface RoleHudRenderer {
    int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom);
}
