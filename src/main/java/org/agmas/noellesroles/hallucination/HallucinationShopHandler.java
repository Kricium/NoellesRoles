package org.agmas.noellesroles.hallucination;

import dev.doctor4t.wathe.api.event.BuildShopEntries;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.ItemStack;
import org.agmas.noellesroles.ModItems;

import java.util.List;
import java.util.Objects;

public final class HallucinationShopHandler {
    private static final String ENTRY_ID = "hallucination_medicine";
    private static final int PRICE = 100;

    private HallucinationShopHandler() {
    }

    public static void register() {
        BuildShopEntries.EVENT.register((player, context) -> {
            HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
            if (!component.hasAnyHallucination() || context.getEntries().isEmpty()) {
                return;
            }

            List<ShopEntry> entries = context.getEntries();
            int replaceIndex = chooseReplaceIndex(player, entries);
            ShopEntry replaced = entries.get(replaceIndex);
            ItemStack medicineStack = ModItems.HALLUCINATION_MEDICINE.getDefaultStack();
            ShopEntry.Builder builder = new ShopEntry.Builder(
                    ENTRY_ID,
                    medicineStack.copy(),
                    PRICE,
                    replaced.type()
            ).actualStack(medicineStack.copy())
                    .onBuy(HallucinationShopHandler::buyHallucinationMedicine);
            if (replaced.hasStockLimit()) {
                builder.stock(replaced.maxStock());
            }
            if (replaced.hasCooldown()) {
                builder.cooldown(replaced.cooldownTicks());
            }
            if (replaced.hasInitialCooldown()) {
                builder.initialCooldown(replaced.initialCooldownTicks());
            }
            context.setEntry(replaceIndex, builder.build());
        });
    }

    private static boolean buyHallucinationMedicine(net.minecraft.entity.player.PlayerEntity player) {
        return ShopEntry.insertStackInFreeSlot(player, ModItems.HALLUCINATION_MEDICINE.getDefaultStack());
    }

    private static int chooseReplaceIndex(net.minecraft.entity.player.PlayerEntity player, List<ShopEntry> entries) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        int seed = component.getShopShuffleSeed();
        if (seed == -1) {
            seed = component.ensureShopShuffleSeed();
        }
        if (seed == -1) {
            return 0;
        }
        return Math.floorMod(Objects.hash(player.getUuid(), seed, entries.size()), entries.size());
    }
}
