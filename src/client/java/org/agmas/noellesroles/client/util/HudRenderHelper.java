package org.agmas.noellesroles.client.util;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.assassin.AssassinPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.commander.CommanderPlayerComponent;
import org.agmas.noellesroles.corruptcop.CorruptCopPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.silencer.SilencerPlayerComponent;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.agmas.noellesroles.taotie.TaotiePlayerComponent;
import org.jetbrains.annotations.Nullable;

public final class HudRenderHelper {
    private HudRenderHelper() {}

    /**
     * Returns the local player if they are in-game and alive, otherwise null.
     */
    @Nullable
    public static ClientPlayerEntity getActivePlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        if (!GameFunctions.isPlayerPlayingAndAlive(client.player)) return null;
        return client.player;
    }

    /**
     * Draws a text line aligned to the bottom-right corner of the screen.
     * Returns the new drawY (above the drawn line) for stacking multiple lines.
     */
    public static int drawBottomRight(DrawContext context, TextRenderer renderer, Text text, int drawY, int color) {
        drawY -= renderer.getWrappedLinesHeight(text, 999999);
        context.drawTextWithShadow(renderer, text, context.getScaledWindowWidth() - renderer.getWidth(text), drawY, color);
        return drawY;
    }

    /**
     * Returns the top Y of the active role's bottom-right ability HUD stack.
     * If the role has no bottom-right ability HUD this frame, the screen height is returned.
     */
    public static int getBottomRightSkillHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());

        if (gameWorld.isRole(player, Noellesroles.ASSASSIN)) {
            return noellesroles$getAssassinHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.COMMANDER)) {
            return noellesroles$getCommanderHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.CORRUPT_COP)) {
            return noellesroles$getCorruptCopHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.MORPHLING)) {
            return noellesroles$getMorphlingHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.PATHOGEN)) {
            return noellesroles$getPathogenHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.SILENCER) && !SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            return noellesroles$getSilencerHudTopY(context, renderer, player);
        }
        if (gameWorld.isRole(player, Noellesroles.TAOTIE)) {
            return noellesroles$getTaotieHudTopY(context, renderer, player);
        }

        return context.getScaledWindowHeight();
    }

    public static int getBottomRightSkillHudRightPadding(ClientPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        return gameWorld.isRole(player, Noellesroles.ASSASSIN) ? 5 : 0;
    }

    private static int noellesroles$getAssassinHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        AssassinPlayerComponent assassinComp = AssassinPlayerComponent.KEY.get(player);
        int drawY = context.getScaledWindowHeight() - 5;

        Text guessesText = Text.translatable(
                "hud.assassin.guesses_remaining",
                assassinComp.getGuessesRemaining(),
                assassinComp.getMaxGuesses()
        );
        drawY -= renderer.getWrappedLinesHeight(guessesText, 999999);

        if (assassinComp.getCooldownTicks() > 0) {
            int cooldownSeconds = (assassinComp.getCooldownTicks() + 19) / 20;
            Text cooldownText = Text.translatable("hud.assassin.cooldown", cooldownSeconds);
            drawY -= renderer.getWrappedLinesHeight(cooldownText, 999999) + 2;
        }

        if (assassinComp.canGuess()) {
            Text hintText = Text.translatable("hud.assassin.press_key_hint", noellesroles$getAbilityKeyName());
            drawY -= renderer.getWrappedLinesHeight(hintText, 999999) + 2;
        } else if (assassinComp.getCooldownTicks() > 0) {
            Text statusText = Text.translatable("hud.assassin.on_cooldown");
            drawY -= renderer.getWrappedLinesHeight(statusText, 999999) + 2;
        } else if (assassinComp.getGuessesRemaining() <= 0) {
            Text statusText = Text.translatable("hud.assassin.no_guesses");
            drawY -= renderer.getWrappedLinesHeight(statusText, 999999) + 2;
        }

        return drawY;
    }

    private static int noellesroles$getCommanderHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);

        int drawY = context.getScaledWindowHeight();
        if (!commanderComp.getThreatTargetNames().isEmpty()) {
            Text line2 = Text.translatable("tip.commander.marked", String.join(", ", commanderComp.getThreatTargetNames()));
            drawY -= renderer.getWrappedLinesHeight(line2, 999999);
        }

        Text line1;
        if (abilityComp.getCooldown() > 0) {
            line1 = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (commanderComp.canMarkMore()) {
            line1 = Text.translatable(
                    "tip.commander.ready",
                    noellesroles$getAbilityKeyText(),
                    commanderComp.getRemainingMarks()
            );
        } else {
            line1 = Text.translatable("tip.commander.no_marks");
        }

        return drawY - renderer.getWrappedLinesHeight(line1, 999999);
    }

    private static int noellesroles$getCorruptCopHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        CorruptCopPlayerComponent corruptCopComponent = CorruptCopPlayerComponent.KEY.get(player);
        if (!corruptCopComponent.isCorruptCopMomentActive()) {
            return context.getScaledWindowHeight();
        }

        int visionCycleTimer = corruptCopComponent.getVisionCycleTimer();
        boolean canSeeThrough = corruptCopComponent.canSeePlayersThroughWalls();
        Text line = canSeeThrough
                ? Text.translatable("tip.corrupt_cop.vision_active", (30 * 20 - visionCycleTimer) / 20)
                : Text.translatable("tip.corrupt_cop.vision_inactive", (20 * 20 - visionCycleTimer) / 20);

        return context.getScaledWindowHeight() - renderer.getWrappedLinesHeight(line, 999999);
    }

    private static int noellesroles$getMorphlingHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        MorphlingPlayerComponent morphlingComp = MorphlingPlayerComponent.KEY.get(player);
        int drawY = context.getScaledWindowHeight();

        int morphTicks = morphlingComp.getMorphTicks();
        Text line;
        if (morphTicks > 0) {
            line = Text.translatable("tip.morphling.active", morphTicks / 20);
        } else if (morphTicks < 0) {
            line = Text.translatable("tip.noellesroles.cooldown", (-morphTicks) / 20);
        } else {
            line = Text.translatable("tip.morphling");
        }
        drawY -= renderer.getWrappedLinesHeight(line, 999999);

        Text corpseHint = Text.translatable(
                morphlingComp.corpseMode ? "tip.morphling.corpse_active" : "tip.morphling.corpse_hint",
                noellesroles$getAbilityKeyText()
        );
        return drawY - renderer.getWrappedLinesHeight(corpseHint, 999999);
    }

    private static int noellesroles$getPathogenHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        Text line = null;

        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        if (NoellesrolesClient.pathogenNearestTarget != null) {
            double distanceSquared = player.squaredDistanceTo(NoellesrolesClient.pathogenNearestTarget);
            boolean canInfect = distanceSquared < 9.0 && player.canSee(NoellesrolesClient.pathogenNearestTarget);
            if (canInfect && abilityComp.getCooldown() <= 0) {
                line = Text.translatable(
                        "tip.pathogen.infect",
                        noellesroles$getAbilityKeyName(),
                        NoellesrolesClient.pathogenNearestTarget.getName().getString()
                );
            }
        }

        if (line == null) {
            return context.getScaledWindowHeight();
        }
        return context.getScaledWindowHeight() - renderer.getWrappedLinesHeight(line, 999999);
    }

    private static int noellesroles$getSilencerHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        SilencerPlayerComponent silencerComp = SilencerPlayerComponent.KEY.get(player);
        Text line;

        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (silencerComp.hasMarkedTarget()) {
            line = Text.translatable(
                    "tip.silencer.confirm",
                    noellesroles$getAbilityKeyName(),
                    silencerComp.getMarkedTargetName(),
                    silencerComp.getMarkTicksRemaining() / 20
            );
        } else if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            line = Text.translatable(
                    "tip.silencer.mark",
                    noellesroles$getAbilityKeyName(),
                    NoellesrolesClient.crosshairTarget.getName().getString()
            );
        } else {
            line = Text.translatable("tip.silencer.ready", noellesroles$getAbilityKeyName());
        }

        return context.getScaledWindowHeight() - renderer.getWrappedLinesHeight(line, 999999);
    }

    private static int noellesroles$getTaotieHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        TaotiePlayerComponent taotieComp = TaotiePlayerComponent.KEY.get(player);
        int drawY = context.getScaledWindowHeight();

        if (taotieComp.isTaotieMomentActive()) {
            Text momentText = Text.translatable("tip.taotie.moment_active", taotieComp.getTaotieMomentTicks() / 20);
            drawY -= renderer.getWrappedLinesHeight(momentText, 999999);
            drawY -= 2;
        }

        if (taotieComp.getSwallowedCount() > 0) {
            Text swallowedText = Text.translatable("tip.taotie.swallowed_count", taotieComp.getSwallowedCount());
            drawY -= renderer.getWrappedLinesHeight(swallowedText, 999999);
            drawY -= 2;
        }

        if (taotieComp.getSwallowCooldown() > 0) {
            Text cooldownText = Text.translatable("tip.noellesroles.cooldown", taotieComp.getSwallowCooldown() / 20);
            drawY -= renderer.getWrappedLinesHeight(cooldownText, 999999);
            drawY -= 2;
        }

        if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            SwallowedPlayerComponent swallowed = SwallowedPlayerComponent.KEY.get(NoellesrolesClient.crosshairTarget);
            if (!swallowed.isSwallowed() && taotieComp.getSwallowCooldown() <= 0) {
                Text swallowHint = Text.translatable(
                        "tip.taotie.swallow",
                        noellesroles$getAbilityKeyName(),
                        NoellesrolesClient.crosshairTarget.getName().getString()
                );
                drawY -= renderer.getWrappedLinesHeight(swallowHint, 999999);
            }
        }

        return drawY;
    }

    private static String noellesroles$getAbilityKeyName() {
        return NoellesrolesClient.abilityBind == null
                ? ""
                : NoellesrolesClient.abilityBind.getBoundKeyLocalizedText().getString();
    }

    private static Text noellesroles$getAbilityKeyText() {
        return NoellesrolesClient.abilityBind == null
                ? Text.empty()
                : NoellesrolesClient.abilityBind.getBoundKeyLocalizedText();
    }
}
