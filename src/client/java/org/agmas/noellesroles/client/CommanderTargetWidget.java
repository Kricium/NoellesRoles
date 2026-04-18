package org.agmas.noellesroles.client;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.RoleScreenHelper;
import org.agmas.noellesroles.client.screen.RoleScreenHelper.InteractionBlocker;
import org.agmas.noellesroles.client.widget.MenuPlayerTargetWidget;
import org.agmas.noellesroles.commander.CommanderPlayerComponent;

import java.util.UUID;
import java.util.function.Consumer;

public class CommanderTargetWidget extends MenuPlayerTargetWidget {
    private static final Text UNKNOWN_PLAYER_TEXT = Text.literal("Unknown");

    private final UUID targetUuid;

    public CommanderTargetWidget(int x, int y, UUID targetUuid, Consumer<UUID> onTargetSelected,
                                 int clipLeft, int clipTop, int clipRight, int clipBottom,
                                 InteractionBlocker... blockers) {
        super(x, y, RoleScreenHelper.getPlayerName(targetUuid, UNKNOWN_PLAYER_TEXT), button -> onTargetSelected.accept(targetUuid),
                clipLeft, clipTop, clipRight, clipBottom, blockers);
        this.targetUuid = targetUuid;
    }

    @Override
    protected SkinTextures getSkinTextures() {
        return RoleScreenHelper.getPlayerSkinTextures(this.targetUuid);
    }

    @Override
    protected ShopEntry.Type getBackgroundType() {
        return ShopEntry.Type.WEAPON;
    }

    @Override
    protected Text getOverlayText() {
        return RoleScreenHelper.getPlayerName(this.targetUuid, UNKNOWN_PLAYER_TEXT);
    }

    @Override
    protected boolean shouldHighlight() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return false;
        }
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        return this.isHovered() || commanderComp.isThreatTarget(this.targetUuid);
    }

    @Override
    protected int getHighlightColor() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return super.getHighlightColor();
        }
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        return commanderComp.isThreatTarget(this.targetUuid) ? 0xAA4B1A8E : super.getHighlightColor();
    }

    @Override
    public boolean shouldRenderTopmostPlayerOverlay() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || !this.visible) {
            return false;
        }
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        return this.isHovered() || commanderComp.isThreatTarget(this.targetUuid);
    }
}
