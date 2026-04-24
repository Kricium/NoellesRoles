package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.agmas.noellesroles.hallucination.HallucinationHelper;

public class HallucinationMedicineItem extends Item {
    public HallucinationMedicineItem(Settings settings) {
        super(settings.food(new net.minecraft.component.type.FoodComponent.Builder()
                .alwaysEdible()
                .nutrition(0)
                .saturationModifier(0.0F)
                .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        if (world.isClient || !(user instanceof PlayerEntity player)) {
            return result;
        }

        HallucinationHelper.tryCleanseFromMedicine(player);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.2F);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            NbtCompound extra = new NbtCompound();
            extra.putString("action", "drink");
            GameRecordManager.recordItemUse(serverPlayer, Registries.ITEM.getId(this), null, extra);
        }
        return result;
    }
}
