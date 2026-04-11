package org.agmas.noellesroles.client.util;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.ModEffects;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.assassin.AssassinPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.commander.CommanderPlayerComponent;
import org.agmas.noellesroles.corruptcop.CorruptCopPlayerComponent;
import org.agmas.noellesroles.criminalreasoner.CriminalReasonerPlayerComponent;
import org.agmas.noellesroles.ferryman.FerrymanPlayerComponent;
import org.agmas.noellesroles.hunter.HunterPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.noisemaker.NoisemakerPlayerComponent;
import org.agmas.noellesroles.recaller.RecallerPlayerComponent;
import org.agmas.noellesroles.reporter.ReporterPlayerComponent;
import org.agmas.noellesroles.silencer.SilencerPlayerComponent;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.agmas.noellesroles.taotie.TaotiePlayerComponent;
import org.agmas.noellesroles.vulture.VulturePlayerComponent;
import org.jetbrains.annotations.Nullable;

public final class HudRenderHelper {
    private static final int UNLIMITED_WIDTH = Integer.MAX_VALUE;
    private static final int LINE_GAP = 2;
    private static final int ASSASSIN_BOTTOM_PADDING = 5;

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
        drawY -= measure(renderer, text);
        context.drawTextWithShadow(renderer, text, context.getScaledWindowWidth() - renderer.getWidth(text), drawY, color);
        return drawY;
    }

    /**
     * Returns the top Y of the active role's bottom-right ability HUD stack.
     * If the role has no bottom-right ability HUD this frame, the screen height is returned.
     */
    public static int getBottomRightSkillHudTopY(DrawContext context, TextRenderer renderer, ClientPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        int bottom = context.getScaledWindowHeight();

        if (gameWorld.isRole(player, Noellesroles.ASSASSIN))    return getAssassinHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.COMMANDER))   return getCommanderHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.CORRUPT_COP)) return getCorruptCopHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.CRIMINAL_REASONER)) return getCriminalReasonerHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.FERRYMAN))    return getFerrymanHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.MORPHLING))   return getMorphlingHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.NOISEMAKER))  return getNoisemakerHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.ORTHOPEDIST)) return getOrthopedistHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.PHANTOM))     return getPhantomHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.PATHOGEN))    return getPathogenHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.RECALLER))    return getRecallerHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.REPORTER))    return getReporterHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.SILENCER))    return getSilencerHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.TAOTIE))      return getTaotieHudTopY(renderer, player, bottom);
        if (gameWorld.isRole(player, Noellesroles.VULTURE))     return getVultureHudTopY(renderer, player, bottom);

        return bottom;
    }

    public static int getBottomRightSkillHudRightPadding(ClientPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        return gameWorld.isRole(player, Noellesroles.ASSASSIN) ? ASSASSIN_BOTTOM_PADDING : 0;
    }

    private static int measure(TextRenderer renderer, Text text) {
        return renderer.getWrappedLinesHeight(text, UNLIMITED_WIDTH);
    }

    /**
     * Moves drawY up by one stacked text line (plus an extra gap above it).
     */
    private static int stackLine(int drawY, TextRenderer renderer, Text text, int gap) {
        return drawY - measure(renderer, text) - gap;
    }

    private static int getAssassinHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AssassinPlayerComponent comp = AssassinPlayerComponent.KEY.get(player);
        int drawY = bottom - ASSASSIN_BOTTOM_PADDING;

        drawY = stackLine(drawY, renderer,
                Text.translatable("hud.assassin.guesses_remaining", comp.getGuessesRemaining(), comp.getMaxGuesses()),
                0);

        if (comp.getCooldownTicks() > 0) {
            int cooldownSeconds = (comp.getCooldownTicks() + 19) / 20;
            drawY = stackLine(drawY, renderer, Text.translatable("hud.assassin.cooldown", cooldownSeconds), LINE_GAP);
        }

        if (comp.canGuess()) {
            drawY = stackLine(drawY, renderer,
                    Text.translatable("hud.assassin.press_key_hint", getAbilityKeyName()), LINE_GAP);
        } else if (comp.getCooldownTicks() > 0) {
            drawY = stackLine(drawY, renderer, Text.translatable("hud.assassin.on_cooldown"), LINE_GAP);
        } else if (comp.getGuessesRemaining() <= 0) {
            drawY = stackLine(drawY, renderer, Text.translatable("hud.assassin.no_guesses"), LINE_GAP);
        }

        return drawY;
    }

    private static int getCommanderHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (!commanderComp.getThreatTargetNames().isEmpty()) {
            drawY = stackLine(drawY, renderer,
                    Text.translatable("tip.commander.marked", String.join(", ", commanderComp.getThreatTargetNames())),
                    0);
        }

        Text line;
        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (commanderComp.canMarkMore()) {
            line = Text.translatable("tip.commander.ready", getAbilityKeyText(), commanderComp.getRemainingMarks());
        } else {
            line = Text.translatable("tip.commander.no_marks");
        }
        return stackLine(drawY, renderer, line, 0);
    }

    private static int getCorruptCopHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CorruptCopPlayerComponent comp = CorruptCopPlayerComponent.KEY.get(player);
        if (!comp.isCorruptCopMomentActive()) return bottom;

        int visionCycleTimer = comp.getVisionCycleTimer();
        Text line = comp.canSeePlayersThroughWalls()
                ? Text.translatable("tip.corrupt_cop.vision_active", (30 * 20 - visionCycleTimer) / 20)
                : Text.translatable("tip.corrupt_cop.vision_inactive", (20 * 20 - visionCycleTimer) / 20);
        return stackLine(bottom, renderer, line, 0);
    }

    private static int getMorphlingHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        MorphlingPlayerComponent comp = MorphlingPlayerComponent.KEY.get(player);

        int morphTicks = comp.getMorphTicks();
        Text statusLine;
        if (morphTicks > 0) {
            statusLine = Text.translatable("tip.morphling.active", morphTicks / 20);
        } else if (morphTicks < 0) {
            statusLine = Text.translatable("tip.noellesroles.cooldown", (-morphTicks) / 20);
        } else {
            statusLine = Text.translatable("tip.morphling");
        }
        int drawY = stackLine(bottom, renderer, statusLine, 0);

        Text corpseHint = Text.translatable(
                comp.corpseMode ? "tip.morphling.corpse_active" : "tip.morphling.corpse_hint",
                getAbilityKeyText());
        return stackLine(drawY, renderer, corpseHint, 0);
    }

    private static int getPhantomHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        Text line = Text.translatable("tip.phantom", getAbilityKeyText());

        var invisEffect = player.getStatusEffect(StatusEffects.INVISIBILITY);
        if (invisEffect != null) {
            line = Text.translatable("tip.phantom.active", invisEffect.getDuration() / 20);
        } else if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static int getCriminalReasonerHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CriminalReasonerPlayerComponent comp = CriminalReasonerPlayerComponent.KEY.get(player);
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);

        Text cooldownText = abilityComp.getCooldown() > 0
                ? Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20)
                : Text.translatable("hud.criminal_reasoner.press_key_hint", getAbilityKeyName());

        int requiredReasoningCount = Math.floorDiv(GameWorldComponent.KEY.get(player.getWorld()).getAllPlayers().size(), 3);
        Text progressText = Text.translatable(
                "hud.criminal_reasoner.progress",
                comp.getSuccessfulReasoningCount(),
                requiredReasoningCount
        );

        int drawY = stackLine(bottom, renderer, cooldownText, 0);
        return stackLine(drawY, renderer, progressText, 0);
    }

    private static int getFerrymanHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        FerrymanPlayerComponent ferryman = FerrymanPlayerComponent.KEY.get(player);
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);

        Text line;
        if (ferryman.isReactionActive()) {
            line = Text.translatable("tip.ferryman.reaction_ready", getAbilityKeyText());
        } else if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else if (NoellesrolesClient.targetBody != null) {
            line = Text.translatable("tip.ferryman.ferry", getAbilityKeyText());
        } else {
            line = Text.translatable("tip.ferryman.progress", ferryman.getFerriedCount(), ferryman.getFerriedRequired(), ferryman.getBlessingStacks());
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static int getPathogenHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
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
                        getAbilityKeyName(),
                        NoellesrolesClient.pathogenNearestTarget.getName().getString());
            }
        }

        return line == null ? bottom : stackLine(bottom, renderer, line, 0);
    }

    private static int getNoisemakerHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        NoisemakerPlayerComponent noisemakerComp = NoisemakerPlayerComponent.KEY.get(player);

        Text line = Text.translatable("tip.noisemaker", getAbilityKeyText());
        if (noisemakerComp.isBroadcasting()) {
            line = Text.translatable("tip.noisemaker.active", noisemakerComp.getBroadcastTicksRemaining() / 20);
        } else if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static int getOrthopedistHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        Text line;

        if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else {
            PlayerEntity target = NoellesrolesClient.crosshairTarget;
            if (target != null && NoellesrolesClient.crosshairTargetDistance <= 3.0 && player.canSee(target)) {
                HunterPlayerComponent hunter = HunterPlayerComponent.KEY.get(target);
                if (hunter.getFractureLayers() > 0) {
                    line = Text.translatable("tip.orthopedist.heal", getAbilityKeyText(), target.getName());
                } else if (!target.hasStatusEffect(ModEffects.BONE_SETTING)) {
                    line = Text.translatable("tip.orthopedist.buff", getAbilityKeyText(), target.getName());
                } else {
                    line = Text.translatable("tip.orthopedist.target_active", target.getName());
                }
            } else {
                line = Text.translatable("tip.orthopedist.no_target");
            }
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static int getSilencerHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        if (SwallowedPlayerComponent.isPlayerSwallowed(player)) return bottom;

        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        SilencerPlayerComponent silencerComp = SilencerPlayerComponent.KEY.get(player);
        Text line;

        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (silencerComp.hasMarkedTarget()) {
            line = Text.translatable(
                    "tip.silencer.confirm",
                    getAbilityKeyName(),
                    silencerComp.getMarkedTargetName(),
                    silencerComp.getMarkTicksRemaining() / 20);
        } else if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            line = Text.translatable(
                    "tip.silencer.mark",
                    getAbilityKeyName(),
                    NoellesrolesClient.crosshairTarget.getName().getString());
        } else {
            line = Text.translatable("tip.silencer.ready", getAbilityKeyName());
        }
        return stackLine(bottom, renderer, line, 0);
    }

    private static int getRecallerHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        RecallerPlayerComponent recaller = RecallerPlayerComponent.KEY.get(player);
        PlayerShopComponent shop = PlayerShopComponent.KEY.get(player);

        Text line = Text.translatable("tip.recaller.teleport", getAbilityKeyText());
        if (!recaller.placed) {
            line = Text.translatable("tip.recaller.place", getAbilityKeyText());
        } else if (shop.balance < 100) {
            line = Text.translatable("tip.recaller.not_enough_money");
        }

        if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static int getReporterHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        ReporterPlayerComponent reporter = ReporterPlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (reporter.hasMarkedTarget()) {
            PlayerEntity markedTarget = player.getWorld().getPlayerByUuid(reporter.getMarkedTarget());
            if (markedTarget != null) {
                Text line2 = Text.translatable("tip.reporter.marked", markedTarget.getName());
                drawY = stackLine(drawY, renderer, line2, 0);
            }
        }

        Text line1;
        if (ability.getCooldown() > 0) {
            line1 = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else {
            PlayerEntity target = NoellesrolesClient.crosshairTarget;
            if (target != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
                line1 = Text.translatable("tip.reporter.target", target.getName(), getAbilityKeyText());
            } else {
                line1 = Text.translatable("tip.reporter.no_target");
            }
        }

        return stackLine(drawY, renderer, line1, 0);
    }

    private static int getTaotieHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        TaotiePlayerComponent comp = TaotiePlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (comp.isTaotieMomentActive()) {
            drawY = stackLine(drawY, renderer,
                    Text.translatable("tip.taotie.moment_active", comp.getTaotieMomentTicks() / 20), LINE_GAP);
        }
        if (comp.getSwallowedCount() > 0) {
            drawY = stackLine(drawY, renderer,
                    Text.translatable("tip.taotie.swallowed_count", comp.getSwallowedCount()), LINE_GAP);
        }
        if (comp.getSwallowCooldown() > 0) {
            drawY = stackLine(drawY, renderer,
                    Text.translatable("tip.noellesroles.cooldown", comp.getSwallowCooldown() / 20), LINE_GAP);
        }

        if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            SwallowedPlayerComponent swallowed = SwallowedPlayerComponent.KEY.get(NoellesrolesClient.crosshairTarget);
            if (!swallowed.isSwallowed() && comp.getSwallowCooldown() <= 0) {
                drawY = stackLine(drawY, renderer,
                        Text.translatable("tip.taotie.swallow",
                                getAbilityKeyName(),
                                NoellesrolesClient.crosshairTarget.getName().getString()),
                        0);
            }
        }
        return drawY;
    }

    private static int getVultureHudTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        VulturePlayerComponent vulture = VulturePlayerComponent.KEY.get(player);
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);

        Text line = Text.translatable("tip.vulture", vulture.getBodiesEaten(), vulture.getBodiesRequired());
        if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        }

        return stackLine(bottom, renderer, line, 0);
    }

    private static String getAbilityKeyName() {
        return NoellesrolesClient.abilityBind == null
                ? ""
                : NoellesrolesClient.abilityBind.getBoundKeyLocalizedText().getString();
    }

    private static Text getAbilityKeyText() {
        return NoellesrolesClient.abilityBind == null
                ? Text.empty()
                : NoellesrolesClient.abilityBind.getBoundKeyLocalizedText();
    }
}
