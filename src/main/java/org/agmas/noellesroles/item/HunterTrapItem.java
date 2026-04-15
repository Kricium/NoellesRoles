package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.agmas.noellesroles.NoellesRolesEntities;
import org.agmas.noellesroles.entity.HunterTrapEntity;
import org.agmas.noellesroles.hunter.HunterPlayerComponent;

import java.util.List;
import java.util.UUID;

public class HunterTrapItem extends Item {
    private static final String POISONED_KEY = "Poisoned";
    private static final double MAX_SURFACE_SEARCH_DEPTH = 2.0D;

    private record TrapPlacement(BlockPos supportPos, Vec3d spawnPos) {}

    public HunterTrapItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        BlockHitResult hitResult = Item.raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (hitResult.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        }

        TrapPlacement placement = findPlacement(world, hitResult.getPos());
        if (placement == null) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            HunterPlayerComponent hunterComponent = HunterPlayerComponent.KEY.get(user);
            for (UUID oldTrapUuid : hunterComponent.removeOldestTrapsIfNeeded()) {
                Entity oldTrap = ((net.minecraft.server.world.ServerWorld) world).getEntity(oldTrapUuid);
                if (oldTrap != null) {
                    oldTrap.discard();
                }
            }
            HunterTrapEntity trap = new HunterTrapEntity(NoellesRolesEntities.HUNTER_TRAP_ENTITY, world);
            trap.refreshPositionAndAngles(placement.spawnPos().x, placement.spawnPos().y, placement.spawnPos().z, 0.0F, 0.0F);
            trap.setOwner(user);
            trap.setSupportPos(placement.supportPos());
            trap.setPoisoned(isPoisoned(stack));
            world.spawnEntity(trap);
            hunterComponent.registerTrap(trap.getUuid());
            world.playSound(null, placement.supportPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.PLAYERS, 0.8F, 1.1F);
            if (user instanceof ServerPlayerEntity serverPlayer) {
                NbtCompound extra = new NbtCompound();
                GameRecordManager.putBlockPos(extra, "pos", placement.supportPos().up());
                extra.putString("action", "place");
                GameRecordManager.recordItemUse(serverPlayer, Registries.ITEM.getId(this), null, extra);
            }
            stack.decrement(1);
        }

        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.hunter_trap.tooltip.line1").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.noellesroles.hunter_trap.tooltip.line2").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.noellesroles.hunter_trap.tooltip.line3").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.noellesroles.hunter_trap.tooltip.line4").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.noellesroles.hunter_trap.tooltip.line5").formatted(Formatting.GRAY));
        if (isPoisoned(stack)) {
            tooltip.add(Text.translatable("item.noellesroles.hunter_trap.poisoned").formatted(Formatting.DARK_GREEN));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    public static boolean isPoisoned(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        return customData != null && customData.copyNbt().getBoolean(POISONED_KEY);
    }

    public static void setPoisoned(ItemStack stack, boolean poisoned) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = customData != null ? customData.copyNbt() : new NbtCompound();
        nbt.putBoolean(POISONED_KEY, poisoned);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static TrapPlacement findPlacement(World world, Vec3d hitPos) {
        int minBlockY = net.minecraft.util.math.MathHelper.floor(hitPos.y - MAX_SURFACE_SEARCH_DEPTH);
        int maxBlockY = net.minecraft.util.math.MathHelper.floor(hitPos.y);

        double bestTopY = Double.NEGATIVE_INFINITY;
        BlockPos bestSupportPos = null;

        for (int y = maxBlockY; y >= minBlockY; y--) {
            BlockPos supportPos = BlockPos.ofFloored(hitPos.x, y, hitPos.z);
            VoxelShape collisionShape = world.getBlockState(supportPos).getCollisionShape(world, supportPos);
            if (collisionShape.isEmpty()) {
                continue;
            }

            double topY = supportPos.getY() + collisionShape.getMax(Direction.Axis.Y);
            if (topY <= bestTopY || hitPos.y < topY - 1.0E-4D) {
                continue;
            }

            Vec3d spawnPos = new Vec3d(supportPos.getX() + 0.5D, topY, supportPos.getZ() + 0.5D);
            Box trapBox = NoellesRolesEntities.HUNTER_TRAP_ENTITY.getDimensions().getBoxAt(spawnPos.x, spawnPos.y, spawnPos.z);
            if (!world.isSpaceEmpty(null, trapBox)) {
                continue;
            }

            bestTopY = topY;
            bestSupportPos = supportPos.toImmutable();
        }

        if (bestSupportPos == null) {
            return null;
        }

        return new TrapPlacement(bestSupportPos, new Vec3d(bestSupportPos.getX() + 0.5D, bestTopY, bestSupportPos.getZ() + 0.5D));
    }

}
