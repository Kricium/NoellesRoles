package org.agmas.noellesroles.client.mixin.roundend;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import dev.doctor4t.wathe.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.List;

@Mixin(RoundTextRenderer.class)
public abstract class RoundTextRendererMixin {
    private static final int BASE_PLAYER_ROWS = 4;
    private static final int DEFAULT_PLAYER_COLUMNS = 6;
    private static final int CONTENT_CENTER_OFFSET_Y = -40;
    private static final int PLAYER_CARD_SPACING_X = 36;
    private static final int PLAYER_CARD_SPACING_Y = 28;
    private static final int WINNER_SECTION_COLOR = 0x55AA55;
    private static final int LOSER_SECTION_COLOR = 0xFF5555;
    private static final int WINNER_TITLE_Y = 14;
    private static final int SECTION_TITLE_GAP = 8;
    private static final int PLAYER_SECTION_START_Y = 16;
    private static final int LOSER_SECTION_GAP_Y = 14;
    private static final int SCREEN_SIDE_MARGIN = 24;
    private static final int SCREEN_TOP_MARGIN = 20;
    private static final int SCREEN_BOTTOM_MARGIN = 12;
    private static final int PLAYER_CARD_TEXT_BOTTOM = 18;
    private static final int ORIGINAL_PLAYER_CARD_BOTTOM = 26;
    private static final int END_TEXT_TOP_Y = -12;
    private static final int SUBTITLE_TOP_Y = -4;
    private static final int ROLE_TEXT_MAX_WIDTH = 36;
    private static final int ROLE_TEXT_LINE_HEIGHT = 9;
    private static final int ROLE_TEXT_MAX_CHARS_PER_LINE = 4;
    private static final int ROLE_TEXT_MIN_FIRST_LINE_CHARS = 2;

    @Shadow
    private static int endTime;

    @Shadow
    private static RoleAnnouncementTexts.RoleAnnouncementText role;

    @Invoker("renderPlayerCard")
    private static void noellesroles$invokeRenderPlayerCard(
            DrawContext context,
            TextRenderer textRenderer,
            GameRoundEndComponent.RoundEndData data,
            int x,
            int y
    ) {
        throw new AssertionError();
    }

    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$renderAdaptiveRoundEnd(
            TextRenderer textRenderer,
            ClientPlayerEntity player,
            DrawContext context,
            CallbackInfo ci
    ) {
        if (player == null || endTime <= 0 || endTime >= 120) {
            return;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorld.isRunning()) {
            return;
        }

        GameRoundEndComponent roundEnd = GameRoundEndComponent.KEY.get(player.getScoreboard());
        if (roundEnd.getWinStatus() == GameFunctions.WinStatus.NONE) {
            return;
        }

        if (roundEnd.getRoundGameMode() == WatheGameModes.DISCOVERY
                || roundEnd.getRoundGameMode() == WatheGameModes.LOOSE_ENDS) {
            return;
        }

        Text endText = noellesroles$getEndText(roundEnd);
        if (endText == null) {
            return;
        }

        List<GameRoundEndComponent.RoundEndData> winners = new ArrayList<>();
        List<GameRoundEndComponent.RoundEndData> losers = new ArrayList<>();
        for (GameRoundEndComponent.RoundEndData playerData : roundEnd.getPlayers()) {
            if (playerData.isWinner()) {
                winners.add(playerData);
            } else {
                losers.add(playerData);
            }
        }

        boolean useOriginalLayout = noellesroles$canUseOriginalLayout(
                winners,
                losers,
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );

        SectionLayout winnerLayout;
        SectionLayout loserLayout;
        int loserTitleY;
        int shiftUp;
        float cardScale;
        if (useOriginalLayout) {
            winnerLayout = noellesroles$createOriginalLayout(winners.size());
            loserLayout = noellesroles$createOriginalLayout(losers.size());
            loserTitleY = noellesroles$getLoserTitleY(winners, winnerLayout, 1.0f);
            shiftUp = 0;
            cardScale = 1.0f;
        } else {
            int maxColumns = noellesroles$getMaxColumns(context.getScaledWindowWidth());
            float[] layoutPlan = noellesroles$findLayoutPlan(
                    winners,
                    losers,
                    maxColumns,
                    context.getScaledWindowHeight()
            );
            int extraColumns = Math.round(layoutPlan[0]);
            winnerLayout = noellesroles$createLayout(winners.size(), maxColumns, extraColumns);
            loserLayout = noellesroles$createLayout(losers.size(), maxColumns, extraColumns);
            loserTitleY = Math.round(layoutPlan[1]);
            shiftUp = Math.round(layoutPlan[2]);
            cardScale = layoutPlan[3];
        }

        context.getMatrices().push();
        context.getMatrices().translate(
                context.getScaledWindowWidth() / 2f,
                context.getScaledWindowHeight() / 2f + CONTENT_CENTER_OFFSET_Y - shiftUp,
                0f
        );

        noellesroles$drawScaledCenteredText(context, textRenderer, endText, 2.6f, END_TEXT_TOP_Y, 0xFFFFFF);

        String subtitleKey = noellesroles$getResultSubtitleKey(roundEnd);
        noellesroles$drawScaledCenteredText(
                context,
                textRenderer,
                Text.translatable(subtitleKey),
                1.2f,
                SUBTITLE_TOP_Y,
                0xFFFFFF
        );

        noellesroles$drawSection(
                context,
                textRenderer,
                null,
                WINNER_SECTION_COLOR,
                winners,
                winnerLayout,
                WINNER_TITLE_Y,
                PLAYER_SECTION_START_Y,
                cardScale
        );

        if (!losers.isEmpty()) {
            noellesroles$drawSection(
                    context,
                    textRenderer,
                    Text.translatable("announcement.result.losers"),
                    LOSER_SECTION_COLOR,
                    losers,
                    loserLayout,
                    loserTitleY,
                    loserTitleY + LOSER_SECTION_GAP_Y,
                    cardScale
            );
        }

        context.getMatrices().pop();
        ci.cancel();
    }

    @WrapOperation(
            method = "renderPlayerCard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"
            )
    )
    private static int noellesroles$wrapRoundEndRoleName(
            DrawContext context,
            TextRenderer textRenderer,
            Text text,
            int x,
            int y,
            int color,
            Operation<Integer> original
    ) {
        List<OrderedText> wrappedLines = noellesroles$wrapRoleName(textRenderer, text);
        if (wrappedLines.size() <= 1) {
            return original.call(context, textRenderer, text, x, y, color);
        }

        int centerX = x + textRenderer.getWidth(text) / 2;
        int currentY = y;
        int drawn = 0;
        for (OrderedText line : wrappedLines) {
            int centeredX = centerX - textRenderer.getWidth(line) / 2;
            drawn = context.drawTextWithShadow(textRenderer, line, centeredX, currentY, color);
            currentY += ROLE_TEXT_LINE_HEIGHT;
        }
        return drawn;
    }

    // 简单缓存：相同的角色名每帧都会调用此方法，缓存避免重复 codePointCount/切分
    @Unique
    private static final java.util.Map<String, List<OrderedText>> WRAP_ROLE_CACHE = new java.util.HashMap<>();

    private static List<OrderedText> noellesroles$wrapRoleName(TextRenderer textRenderer, Text text) {
        String raw = text.getString();
        List<OrderedText> cached = WRAP_ROLE_CACHE.get(raw);
        if (cached != null) {
            return cached;
        }

        int totalChars = raw.codePointCount(0, raw.length());
        List<OrderedText> result;
        if (totalChars <= ROLE_TEXT_MAX_CHARS_PER_LINE) {
            result = textRenderer.wrapLines(text, ROLE_TEXT_MAX_WIDTH);
        } else {
            List<OrderedText> lines = new ArrayList<>();
            int firstLineChars = Math.max(ROLE_TEXT_MIN_FIRST_LINE_CHARS, totalChars - ROLE_TEXT_MAX_CHARS_PER_LINE);
            firstLineChars = Math.min(firstLineChars, totalChars - 1);

            String firstLine = noellesroles$substringByCodePoints(raw, 0, firstLineChars);
            String secondLine = noellesroles$substringByCodePoints(raw, firstLineChars, totalChars);
            lines.add(Text.literal(firstLine).setStyle(text.getStyle()).asOrderedText());
            lines.add(Text.literal(secondLine).setStyle(text.getStyle()).asOrderedText());
            result = lines;
        }

        // 防止缓存无限增长（角色名有限，正常不会触发）
        if (WRAP_ROLE_CACHE.size() > 64) {
            WRAP_ROLE_CACHE.clear();
        }
        WRAP_ROLE_CACHE.put(raw, result);
        return result;
    }

    @Unique
    private static String noellesroles$substringByCodePoints(String text, int beginIndex, int endIndex) {
        int beginOffset = text.offsetByCodePoints(0, beginIndex);
        int endOffset = text.offsetByCodePoints(0, endIndex);
        return text.substring(beginOffset, endOffset);
    }

    @Unique
    private static Text noellesroles$getEndText(GameRoundEndComponent roundEnd) {
        if (roundEnd.getWinStatus() == GameFunctions.WinStatus.KILLERS) {
            return Text.translatable("shared.win.killers").withColor(RoleAnnouncementTexts.KILLER.colour);
        }
        if (roundEnd.getWinStatus() == GameFunctions.WinStatus.PASSENGERS) {
            return Text.translatable("shared.win.passengers").withColor(RoleAnnouncementTexts.CIVILIAN.colour);
        }
        if (roundEnd.getWinStatus() == GameFunctions.WinStatus.NEUTRAL) {
            for (GameRoundEndComponent.RoundEndData data : roundEnd.getPlayers()) {
                if (data.isWinner()) {
                    return RoleAnnouncementTexts.getForRole(data.role()).winText;
                }
            }
            return null;
        }

        return role.getEndText(roundEnd.getWinStatus(), Text.empty());
    }

    @Unique
    private static String noellesroles$getResultSubtitleKey(GameRoundEndComponent roundEnd) {
        if (roundEnd.getWinStatus() == GameFunctions.WinStatus.NEUTRAL) {
            for (GameRoundEndComponent.RoundEndData data : roundEnd.getPlayers()) {
                if (data.isWinner()) {
                    return "game.win." + data.role().getPath();
                }
            }
        }

        return "game.win." + roundEnd.getWinStatus().name().toLowerCase();
    }

    @Unique
    private static void noellesroles$drawScaledCenteredText(
            DrawContext context,
            TextRenderer textRenderer,
            Text text,
            float scale,
            int y,
            int color
    ) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);
        context.drawTextWithShadow(textRenderer, text, -textRenderer.getWidth(text) / 2, y, color);
        context.getMatrices().pop();
    }

    @Unique
    private static void noellesroles$drawSection(
            DrawContext context,
            TextRenderer textRenderer,
            Text title,
            int titleColor,
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout,
            int titleY,
            int cardsStartY,
            float cardScale
    ) {
        if (title != null) {
            context.drawTextWithShadow(textRenderer, title, -textRenderer.getWidth(title) / 2, titleY, titleColor);
        }

        for (int index = 0; index < players.size(); index++) {
            GameRoundEndComponent.RoundEndData data = players.get(index);
            int row = index / layout.columns();
            int column = index % layout.columns();
            int columnsThisRow = row == layout.rows() - 1
                    ? noellesroles$getLastRowColumns(players.size(), layout.columns())
                    : layout.columns();
            int rowWidth = columnsThisRow * PLAYER_CARD_SPACING_X;
            int startX = -rowWidth / 2;
            int x = noellesroles$scaleCoordinate(
                    startX + column * PLAYER_CARD_SPACING_X + PLAYER_CARD_SPACING_X / 2 - 8,
                    cardScale
            );
            int rowOffset = row * PLAYER_CARD_SPACING_Y + noellesroles$getAccumulatedExtraHeightBeforeRow(players, layout, row);
            int y = cardsStartY + noellesroles$scaleCoordinate(rowOffset, cardScale);

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0f);
            context.getMatrices().scale(cardScale, cardScale, 1f);
            noellesroles$invokeRenderPlayerCard(context, textRenderer, data, 0, 0);
            context.getMatrices().pop();
        }
    }

    @Unique
    private static int noellesroles$getLastRowColumns(int playerCount, int columns) {
        if (playerCount == 0) {
            return 0;
        }

        int remainder = playerCount % columns;
        return remainder == 0 ? columns : remainder;
    }

    @Unique
    private static int noellesroles$getSectionBottom(
            int cardsStartY,
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout,
            float cardScale
    ) {
        if (players.isEmpty() || layout.rows() <= 0) {
            return cardsStartY + noellesroles$scaleCoordinate(PLAYER_CARD_TEXT_BOTTOM, cardScale);
        }

        int lastRow = layout.rows() - 1;
        int rowOffset = lastRow * PLAYER_CARD_SPACING_Y + noellesroles$getAccumulatedExtraHeightBeforeRow(players, layout, lastRow);
        int rowBottom = rowOffset + PLAYER_CARD_TEXT_BOTTOM + noellesroles$getRowExtraHeight(players, layout, lastRow);
        return cardsStartY + noellesroles$scaleCoordinate(rowBottom, cardScale);
    }

    @Unique
    private static boolean noellesroles$canUseOriginalLayout(
            List<GameRoundEndComponent.RoundEndData> winners,
            List<GameRoundEndComponent.RoundEndData> losers,
            int screenWidth,
            int screenHeight
    ) {
        SectionLayout winnerLayout = noellesroles$createOriginalLayout(winners.size());
        SectionLayout loserLayout = noellesroles$createOriginalLayout(losers.size());

        if (!noellesroles$sectionFitsOriginalWidth(winners.size(), winnerLayout, screenWidth)) {
            return false;
        }

        if (!noellesroles$sectionFitsOriginalWidth(losers.size(), loserLayout, screenWidth)) {
            return false;
        }

        int localVisibleBottom = screenHeight - (screenHeight / 2 + CONTENT_CENTER_OFFSET_Y);
        int contentBottom = losers.isEmpty()
                ? noellesroles$getSectionBottom(PLAYER_SECTION_START_Y, winners, winnerLayout, 1.0f)
                : noellesroles$getSectionBottom(
                noellesroles$getLoserTitleY(winners, winnerLayout, 1.0f) + LOSER_SECTION_GAP_Y,
                losers,
                loserLayout,
                1.0f
        );
        return contentBottom <= localVisibleBottom;
    }

    @Unique
    private static boolean noellesroles$sectionFitsOriginalWidth(
            int playerCount,
            SectionLayout layout,
            int screenWidth
    ) {
        for (int row = 0; row < layout.rows(); row++) {
            int columnsThisRow = row == layout.rows() - 1
                    ? noellesroles$getLastRowColumns(playerCount, layout.columns())
                    : layout.columns();
            if (columnsThisRow * PLAYER_CARD_SPACING_X > screenWidth) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static SectionLayout noellesroles$createOriginalLayout(int playerCount) {
        if (playerCount <= 0) {
            return new SectionLayout(1, 0);
        }

        int columns = Math.max(DEFAULT_PLAYER_COLUMNS, (playerCount + BASE_PLAYER_ROWS - 1) / BASE_PLAYER_ROWS);
        int rows = Math.min(BASE_PLAYER_ROWS, (playerCount + columns - 1) / columns);
        return new SectionLayout(columns, rows);
    }

    @Unique
    private static float[] noellesroles$findLayoutPlan(
            List<GameRoundEndComponent.RoundEndData> winners,
            List<GameRoundEndComponent.RoundEndData> losers,
            int maxColumns,
            int screenHeight
    ) {
        int localVisibleTop = SCREEN_TOP_MARGIN - (screenHeight / 2 + CONTENT_CENTER_OFFSET_Y);
        int localVisibleBottom = screenHeight - SCREEN_BOTTOM_MARGIN - (screenHeight / 2 + CONTENT_CENTER_OFFSET_Y);
        int minContentTop = Math.min(END_TEXT_TOP_Y, SUBTITLE_TOP_Y);
        int maxShiftUp = Math.max(0, minContentTop - localVisibleTop);

        float[] fallbackPlan = null;
        for (int extraColumns = 0; extraColumns < maxColumns; extraColumns++) {
            SectionLayout winnerLayout = noellesroles$createLayout(winners.size(), maxColumns, extraColumns);
            SectionLayout loserLayout = noellesroles$createLayout(losers.size(), maxColumns, extraColumns);
            int loserTitleY = noellesroles$getLoserTitleY(winners, winnerLayout, 1.0f);
            int contentBottom = losers.isEmpty()
                    ? noellesroles$getSectionBottom(PLAYER_SECTION_START_Y, winners, winnerLayout, 1.0f)
                    : noellesroles$getSectionBottom(loserTitleY + LOSER_SECTION_GAP_Y, losers, loserLayout, 1.0f);
            int requiredShiftUp = Math.max(0, contentBottom - localVisibleBottom);
            int appliedShiftUp = Math.min(requiredShiftUp, maxShiftUp);
            float[] candidatePlan = new float[]{extraColumns, loserTitleY, appliedShiftUp, 1.0f};
            fallbackPlan = candidatePlan;
            if (contentBottom - appliedShiftUp <= localVisibleBottom) {
                return candidatePlan;
            }
        }

        if (fallbackPlan == null) {
            return new float[]{0f, PLAYER_SECTION_START_Y + PLAYER_CARD_SPACING_Y + SECTION_TITLE_GAP, 0f, 1.0f};
        }

        int fallbackExtraColumns = Math.round(fallbackPlan[0]);
        SectionLayout winnerLayout = noellesroles$createLayout(winners.size(), maxColumns, fallbackExtraColumns);
        SectionLayout loserLayout = noellesroles$createLayout(losers.size(), maxColumns, fallbackExtraColumns);
        float maxScale = noellesroles$computeCardScaleToFit(
                winners,
                winnerLayout,
                losers,
                loserLayout,
                localVisibleBottom + Math.round(fallbackPlan[2])
        );
        int scaledLoserTitleY = noellesroles$getLoserTitleY(winners, winnerLayout, maxScale);
        return new float[]{fallbackPlan[0], scaledLoserTitleY, fallbackPlan[2], maxScale};
    }

    @Unique
    private static SectionLayout noellesroles$createLayout(int playerCount, int maxColumns, int extraColumns) {
        if (playerCount <= 0) {
            return new SectionLayout(1, 0);
        }

        int columns = noellesroles$getBaseColumns(playerCount, maxColumns);
        columns = Math.min(Math.max(1, maxColumns), columns + extraColumns);
        int rows = (playerCount + columns - 1) / columns;
        return new SectionLayout(columns, rows);
    }

    @Unique
    private static int noellesroles$getBaseColumns(int playerCount, int maxColumns) {
        int columns = Math.max(1, (playerCount + BASE_PLAYER_ROWS - 1) / BASE_PLAYER_ROWS);
        return Math.min(columns, Math.max(1, Math.min(DEFAULT_PLAYER_COLUMNS, maxColumns)));
    }

    @Unique
    private static float noellesroles$computeCardScaleToFit(
            List<GameRoundEndComponent.RoundEndData> winners,
            SectionLayout winnerLayout,
            List<GameRoundEndComponent.RoundEndData> losers,
            SectionLayout loserLayout,
            int availableBottom
    ) {
        boolean hasLosers = !losers.isEmpty();
        float fixedHeight = hasLosers
                ? PLAYER_SECTION_START_Y + SECTION_TITLE_GAP + LOSER_SECTION_GAP_Y
                : PLAYER_SECTION_START_Y;
        float scalableHeight = hasLosers
                ? noellesroles$getNextRowStartOffset(winners, winnerLayout)
                + noellesroles$getSectionBottomOffset(losers, loserLayout)
                : noellesroles$getSectionBottomOffset(winners, winnerLayout);

        if (scalableHeight <= 0f) {
            return 1.0f;
        }

        float scale = (availableBottom - fixedHeight) / scalableHeight;
        return Math.max(0.05f, Math.min(1.0f, scale));
    }

    @Unique
    private static int noellesroles$getLoserTitleY(
            List<GameRoundEndComponent.RoundEndData> winners,
            SectionLayout winnerLayout,
            float cardScale
    ) {
        return PLAYER_SECTION_START_Y
                + noellesroles$scaleCoordinate(noellesroles$getNextRowStartOffset(winners, winnerLayout), cardScale)
                + SECTION_TITLE_GAP;
    }

    @Unique
    private static int noellesroles$getSectionBottomOffset(
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout
    ) {
        if (players.isEmpty() || layout.rows() <= 0) {
            return PLAYER_CARD_TEXT_BOTTOM;
        }

        int lastRow = layout.rows() - 1;
        int rowOffset = lastRow * PLAYER_CARD_SPACING_Y + noellesroles$getAccumulatedExtraHeightBeforeRow(players, layout, lastRow);
        return rowOffset + PLAYER_CARD_TEXT_BOTTOM + noellesroles$getRowExtraHeight(players, layout, lastRow);
    }

    @Unique
    private static int noellesroles$getNextRowStartOffset(
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout
    ) {
        return Math.max(1, layout.rows()) * PLAYER_CARD_SPACING_Y
                + noellesroles$getAccumulatedExtraHeightBeforeRow(players, layout, layout.rows());
    }

    @Unique
    private static int noellesroles$getAccumulatedExtraHeightBeforeRow(
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout,
            int row
    ) {
        int extraHeight = 0;
        for (int previousRow = 0; previousRow < row; previousRow++) {
            extraHeight += noellesroles$getRowExtraHeight(players, layout, previousRow);
        }
        return extraHeight;
    }

    @Unique
    private static int noellesroles$getRowExtraHeight(
            List<GameRoundEndComponent.RoundEndData> players,
            SectionLayout layout,
            int row
    ) {
        if (row < 0 || row >= layout.rows()) {
            return 0;
        }

        int startIndex = row * layout.columns();
        int endIndex = Math.min(players.size(), startIndex + layout.columns());
        int maxExtraHeight = 0;
        for (int index = startIndex; index < endIndex; index++) {
            int lineCount = noellesroles$getRoleLineCount(players.get(index));
            maxExtraHeight = Math.max(maxExtraHeight, Math.max(0, lineCount - 1) * ROLE_TEXT_LINE_HEIGHT);
        }
        return maxExtraHeight;
    }

    @Unique
    private static int noellesroles$getRoleLineCount(GameRoundEndComponent.RoundEndData data) {
        String roleName = RoleAnnouncementTexts.getForRole(data.role()).roleText.getString();
        return roleName.codePointCount(0, roleName.length()) > ROLE_TEXT_MAX_CHARS_PER_LINE ? 2 : 1;
    }

    @Unique
    private static int noellesroles$scaleCoordinate(int value, float scale) {
        return Math.round(value * scale);
    }

    @Unique
    private static int noellesroles$getMaxColumns(int screenWidth) {
        int usableWidth = Math.max(PLAYER_CARD_SPACING_X, screenWidth - SCREEN_SIDE_MARGIN * 2);
        return Math.max(1, usableWidth / PLAYER_CARD_SPACING_X);
    }

    @Unique
    private record SectionLayout(int columns, int rows) {
    }
}
