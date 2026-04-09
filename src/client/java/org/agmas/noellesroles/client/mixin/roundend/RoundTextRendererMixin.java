package org.agmas.noellesroles.client.mixin.roundend;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RoundTextRenderer.class)
public abstract class RoundTextRendererMixin {
    private static final int ROLE_TEXT_MAX_WIDTH = 36;
    private static final int ROLE_TEXT_LINE_HEIGHT = 9;
    private static final int MULTILINE_ROLE_TEXT_OFFSET_X = 0;
    private static final int MULTILINE_ROLE_TEXT_OFFSET_Y = -8;
    private static final int ROLE_TEXT_MAX_CHARS_PER_LINE = 3;

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
        if (wrappedLines.isEmpty()) {
            return original.call(context, textRenderer, text, x, y, color);
        }

        if (wrappedLines.size() == 1) {
            return original.call(context, textRenderer, text, x, y, color);
        }

        int lineCount = wrappedLines.size();
        int centerX = x + textRenderer.getWidth(text) / 2 + MULTILINE_ROLE_TEXT_OFFSET_X;
        int startY = y + MULTILINE_ROLE_TEXT_OFFSET_Y;
        int currentY = startY;
        int drawn = 0;
        for (OrderedText line : wrappedLines) {
            int centeredX = centerX - textRenderer.getWidth(line) / 2;
            drawn = context.drawTextWithShadow(textRenderer, line, centeredX, currentY, color);
            currentY += ROLE_TEXT_LINE_HEIGHT;
        }
        return drawn;
    }

    private static List<OrderedText> noellesroles$wrapRoleName(TextRenderer textRenderer, Text text) {
        String raw = text.getString();
        if (raw.codePointCount(0, raw.length()) <= ROLE_TEXT_MAX_CHARS_PER_LINE) {
            return textRenderer.wrapLines(text, ROLE_TEXT_MAX_WIDTH);
        }

        List<OrderedText> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentCount = 0;

        for (int offset = 0; offset < raw.length(); ) {
            int codePoint = raw.codePointAt(offset);
            currentLine.appendCodePoint(codePoint);
            currentCount++;
            offset += Character.charCount(codePoint);

            if (currentCount >= ROLE_TEXT_MAX_CHARS_PER_LINE) {
                lines.add(Text.literal(currentLine.toString()).asOrderedText());
                currentLine.setLength(0);
                currentCount = 0;
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(Text.literal(currentLine.toString()).asOrderedText());
        }

        return lines;
    }
}
