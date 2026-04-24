package org.agmas.noellesroles.mixin.hallucination;

import dev.doctor4t.wathe.block.FoodPlatterBlock;
import dev.doctor4t.wathe.block_entity.BeveragePlateBlockEntity;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.agmas.noellesroles.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FoodPlatterBlock.class)
public abstract class HallucinationMedicinePlateMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void noellesroles$useHallucinationMedicinePlate(BlockState state,
                                                            World world,
                                                            BlockPos pos,
                                                            PlayerEntity player,
                                                            BlockHitResult hit,
                                                            CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient || player.isCreative()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BeveragePlateBlockEntity plate)) {
            return;
        }

        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        if (handStack.isOf(ModItems.HALLUCINATION_MEDICINE)) {
            plate.addItem(handStack.copy());
            if (player instanceof ServerPlayerEntity serverPlayer) {
                NbtCompound extra = new NbtCompound();
                extra.putString("action", "place");
                GameRecordManager.putBlockPos(extra, "pos", pos);
                GameRecordManager.recordItemUse(serverPlayer, Registries.ITEM.getId(ModItems.HALLUCINATION_MEDICINE), null, extra);
            }
            handStack.decrement(1);
            player.playSoundToPlayer(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if (!handStack.isEmpty()) {
            return;
        }
        if (noellesroles$tryTakeMedicine(plate.getStoredItems(), player, plate, pos, state, world, cir)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Unique
    private static boolean noellesroles$tryTakeMedicine(List<ItemStack> platterItems,
                                                        PlayerEntity player,
                                                        BeveragePlateBlockEntity plate,
                                                        BlockPos pos,
                                                        BlockState state,
                                                        World world,
                                                        CallbackInfoReturnable<ActionResult> cir) {
        for (int i = 0; i < platterItems.size(); i++) {
            if (!platterItems.get(i).isOf(ModItems.HALLUCINATION_MEDICINE)) {
                continue;
            }
            ItemStack taken = platterItems.remove(i).copy();
            taken.setCount(1);
            taken.set(DataComponentTypes.MAX_STACK_SIZE, 1);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                GameRecordManager.recordPlatterTake(serverPlayer, Registries.ITEM.getId(ModItems.HALLUCINATION_MEDICINE), pos, null, null);
            }
            player.playSoundToPlayer(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.setStackInHand(Hand.MAIN_HAND, taken);
            plate.markDirty();
            world.updateListeners(pos, state, state, 3);
            return true;
        }
        return false;
    }
}
