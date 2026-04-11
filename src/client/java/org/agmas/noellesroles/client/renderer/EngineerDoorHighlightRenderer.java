package org.agmas.noellesroles.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.doctor4t.wathe.block.SmallDoorBlock;
import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import dev.doctor4t.wathe.block_entity.SmallDoorBlockEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 工程师门高亮描边渲染器
 * 当门被撬/被堵时，在客户端渲染5秒红色透视描边。
 *
 * 使用 DEBUG_LINES + POSITION_COLOR 格式，不依赖 rendertype_lines shader 的法线展开，
 * 确保所有 12 条边都可靠渲染。
 */
public class EngineerDoorHighlightRenderer {

    private static final int HIGHLIGHT_DURATION_TICKS = 100; // 5 seconds
    private static final float RED = 1.0f;
    private static final float GREEN = 0.19f;
    private static final float BLUE = 0.19f;
    private static final float ALPHA = 1.0f;

    // 门框在模型局部坐标下的包围盒（单位：block）
    // 模型 cuboid(-8, -32, -1) to (8, 0, 1) → ÷16 → (-0.5, -2, -0.0625) to (0.5, 0, 0.0625)
    private static final float DOOR_MIN_X = -0.5f;
    private static final float DOOR_MAX_X = 0.5f;
    private static final float DOOR_MIN_Y = -2.0f;
    private static final float DOOR_MAX_Y = 0.0f;
    private static final float DOOR_MIN_Z = -0.0625f;
    private static final float DOOR_MAX_Z = 0.0625f;

    // 开门动画的滑动距离（14 model units = 14/16 block）
    private static final float OPEN_SLIDE_OFFSET = 14.0f / 16.0f;

    private static final List<HighlightEntry> highlightEntries = new ArrayList<>();

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(EngineerDoorHighlightRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> clientTick());
    }

    public static void onPacketReceived(BlockPos pos) {
        for (HighlightEntry entry : highlightEntries) {
            if (entry.pos.equals(pos)) {
                entry.remainingTicks = HIGHLIGHT_DURATION_TICKS;
                return;
            }
        }
        highlightEntries.add(new HighlightEntry(pos, HIGHLIGHT_DURATION_TICKS));
    }

    private static void clientTick() {
        Iterator<HighlightEntry> iter = highlightEntries.iterator();
        while (iter.hasNext()) {
            HighlightEntry entry = iter.next();
            entry.remainingTicks--;
            if (entry.remainingTicks <= 0) {
                iter.remove();
            }
        }
    }

    public static void clear() {
        highlightEntries.clear();
    }

    private static void render(WorldRenderContext context) {
        if (highlightEntries.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        // GL 状态：禁用深度测试（透视）、position_color shader、DEBUG_LINES
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(5.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        boolean hasVertices = false;

        for (HighlightEntry entry : highlightEntries) {
            BlockPos pos = entry.pos;

            BlockPos lowerPos = getLowerDoorPos(client, pos);
            if (lowerPos == null) continue;

            BlockEntity be = client.world.getBlockEntity(lowerPos);
            if (!(be instanceof DoorBlockEntity doorEntity)) continue;

            renderDoorFrameOutline(matrices, buffer, cameraPos, lowerPos, doorEntity);
            hasVertices = true;
        }

        if (hasVertices) {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } else {
            buffer.end().close();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
    }

    /**
     * 渲染门框描边 — 复用 SmallDoorBlockEntityRenderer 的变换链
     */
    private static void renderDoorFrameOutline(MatrixStack matrices, BufferBuilder buffer,
                                                Vec3d cameraPos, BlockPos lowerPos,
                                                DoorBlockEntity doorEntity) {
        matrices.push();

        // 1. 平移到方块位置（相机相对坐标）+ 渲染器的 translate(0.5, 1.5, 0.5)
        double dx = lowerPos.getX() + 0.5 - cameraPos.x;
        double dy = lowerPos.getY() + 1.5 - cameraPos.y;
        double dz = lowerPos.getZ() + 0.5 - cameraPos.z;
        matrices.translate(dx, dy, dz);

        // 2. 翻转 Y（与渲染器一致）
        matrices.scale(1, -1, 1);

        // 3. ModelPart pivot 偏移 (0, 24/16, 0) = (0, 1.5, 0)
        matrices.translate(0, 1.5, 0);

        // 4. 门的朝向旋转
        float yaw = doorEntity.getYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

        // 5. 开门滑动偏移
        if (doorEntity.isOpen()) {
            matrices.translate(OPEN_SLIDE_OFFSET, 0, 0);
        }

        // 6. 绘制完整的 12 条边框线
        drawBoxOutline(matrices, buffer,
                DOOR_MIN_X, DOOR_MIN_Y, DOOR_MIN_Z,
                DOOR_MAX_X, DOOR_MAX_Y, DOOR_MAX_Z);

        matrices.pop();
    }

    /**
     * 绘制 box 的全部 12 条边（DEBUG_LINES + POSITION_COLOR 格式，无需法线）
     */
    private static void drawBoxOutline(MatrixStack matrices, BufferBuilder buffer,
                                        float x1, float y1, float z1,
                                        float x2, float y2, float z2) {
        Matrix4f mat = matrices.peek().getPositionMatrix();

        // 底面 4 条边
        line(buffer, mat, x1, y1, z1, x2, y1, z1);
        line(buffer, mat, x2, y1, z1, x2, y1, z2);
        line(buffer, mat, x2, y1, z2, x1, y1, z2);
        line(buffer, mat, x1, y1, z2, x1, y1, z1);

        // 顶面 4 条边
        line(buffer, mat, x1, y2, z1, x2, y2, z1);
        line(buffer, mat, x2, y2, z1, x2, y2, z2);
        line(buffer, mat, x2, y2, z2, x1, y2, z2);
        line(buffer, mat, x1, y2, z2, x1, y2, z1);

        // 竖直 4 条边
        line(buffer, mat, x1, y1, z1, x1, y2, z1);
        line(buffer, mat, x2, y1, z1, x2, y2, z1);
        line(buffer, mat, x2, y1, z2, x2, y2, z2);
        line(buffer, mat, x1, y1, z2, x1, y2, z2);
    }

    private static void line(BufferBuilder buffer, Matrix4f mat,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2) {
        buffer.vertex(mat, x1, y1, z1).color(RED, GREEN, BLUE, ALPHA);
        buffer.vertex(mat, x2, y2, z2).color(RED, GREEN, BLUE, ALPHA);
    }

    private static BlockPos getLowerDoorPos(MinecraftClient client, BlockPos pos) {
        BlockState state = client.world.getBlockState(pos);
        if (state.getBlock() instanceof SmallDoorBlock) {
            if (state.contains(SmallDoorBlock.HALF)) {
                return state.get(SmallDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
            }
        }
        if (client.world.getBlockEntity(pos) instanceof SmallDoorBlockEntity) {
            return pos;
        }
        if (client.world.getBlockEntity(pos.down()) instanceof SmallDoorBlockEntity) {
            return pos.down();
        }
        return null;
    }

    private static class HighlightEntry {
        final BlockPos pos;
        int remainingTicks;

        HighlightEntry(BlockPos pos, int remainingTicks) {
            this.pos = pos;
            this.remainingTicks = remainingTicks;
        }
    }
}

