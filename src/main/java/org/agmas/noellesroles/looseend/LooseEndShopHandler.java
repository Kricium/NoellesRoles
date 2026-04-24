package org.agmas.noellesroles.looseend;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.api.event.BuildShopEntries;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.agmas.noellesroles.ModItems;

import java.util.List;

public class LooseEndShopHandler {
    private static final String LOCKPICK_ID = "lockpick";
    private static final String CROWBAR_ID = "crowbar";
    private static final String REPLACE_HINT_KEY = "shop.noellesroles.loose_end.replace_hint";
    private static final String TIMED_BOMB_HINT_KEY = "shop.noellesroles.loose_end.timed_bomb_hint";
    private static final String THROWING_AXE_HINT_KEY = "shop.noellesroles.loose_end.throwing_axe_hint";
    private static final String RIOT_SHIELD_HINT_KEY = "shop.noellesroles.loose_end.riot_shield_hint";
    private static final String REVOLVER_HINT_KEY = "shop.noellesroles.loose_end.revolver_hint";
    private static final String COOLDOWN_DENY_KEY = "shop.error.noellesroles.loose_end.cooling_down";
    private static final String ALREADY_OWNED_DENY_KEY = "shop.error.noellesroles.loose_end.already_owned";

    public static void register() {
        BuildShopEntries.EVENT.register((player, context) -> {
            if (!isLooseEnd(player)) {
                return;
            }

            context.clearEntries();
            context.addEntry(buildLockpickEntry());
            context.addEntry(buildCrowbarEntry());
            context.addEntry(new ShopEntry.Builder("repair_tool", ModItems.REPAIR_TOOL.getDefaultStack(), 100, ShopEntry.Type.TOOL)
                    .stock(1)
                    .build());
            context.addEntry(new ShopEntry.Builder("antidote", ModItems.ANTIDOTE.getDefaultStack(), 50, ShopEntry.Type.TOOL)
                    .stock(1)
                    .build());
            context.addEntry(new ShopEntry.Builder("adrenaline", ModItems.ADRENALINE.getDefaultStack(), 50, ShopEntry.Type.TOOL)
                    .build());
            context.addEntry(new ShopEntry.Builder("poison_needle", ModItems.POISON_NEEDLE.getDefaultStack(), 50, ShopEntry.Type.WEAPON)
                    .stock(1)
                    .build());
            context.addEntry(new ShopEntry.Builder("double_barrel_shotgun", ModItems.DOUBLE_BARREL_SHOTGUN.getDefaultStack(), 50, ShopEntry.Type.WEAPON)
                    .stock(1)
                    .build());
            context.addEntry(new ShopEntry.Builder("poison_vial", WatheItems.POISON_VIAL.getDefaultStack(), 50, ShopEntry.Type.POISON)
                    .build());
            context.addEntry(new ShopEntry.Builder("hunter_trap", ModItems.HUNTER_TRAP.getDefaultStack(), 50, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("timed_bomb", createDisplayStack(ModItems.TIMED_BOMB, TIMED_BOMB_HINT_KEY), 50, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("double_barrel_shell", ModItems.DOUBLE_BARREL_SHELL.getDefaultStack(), 50, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("throwing_axe", createDisplayStack(ModItems.THROWING_AXE, THROWING_AXE_HINT_KEY), 100, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("poison_gas_bomb", ModItems.POISON_GAS_BOMB.getDefaultStack(), 100, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("riot_shield", createDisplayStack(ModItems.RIOT_SHIELD, RIOT_SHIELD_HINT_KEY), 150, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("grenade", WatheItems.GRENADE.getDefaultStack(), 150, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("derringer", WatheItems.DERRINGER.getDefaultStack(), 100, ShopEntry.Type.WEAPON)
                    .stock(1)
                    .build());
            context.addEntry(new ShopEntry.Builder("revolver", createDisplayStack(WatheItems.REVOLVER, REVOLVER_HINT_KEY), 150, ShopEntry.Type.WEAPON)
                    .build());
            context.addEntry(new ShopEntry.Builder("portable_radar", ModItems.PORTABLE_RADAR.getDefaultStack(), 100, ShopEntry.Type.TOOL)
                    .build());
            context.addEntry(new ShopEntry.Builder("medical_splint", ModItems.MEDICAL_SPLINT.getDefaultStack(), 100, ShopEntry.Type.TOOL)
                    .build());
        });
    }

    private static boolean isLooseEnd(PlayerEntity player) {
        return dev.doctor4t.wathe.cca.GameWorldComponent.KEY.get(player.getWorld()).isRole(player, WatheRoles.LOOSE_END);
    }

    private static ShopEntry buildLockpickEntry() {
        ItemStack displayStack = createDisplayStack(WatheItems.LOCKPICK, REPLACE_HINT_KEY);
        return new ShopEntry.Builder(LOCKPICK_ID, displayStack, 50, ShopEntry.Type.TOOL)
                .onBuy(LooseEndShopHandler::buyLockpick)
                .build();
    }

    private static ShopEntry buildCrowbarEntry() {
        ItemStack displayStack = createDisplayStack(WatheItems.CROWBAR, REPLACE_HINT_KEY);
        return new ShopEntry.Builder(CROWBAR_ID, displayStack, 50, ShopEntry.Type.TOOL)
                .onBuy(LooseEndShopHandler::buyCrowbar)
                .build();
    }

    private static ItemStack createDisplayStack(Item item, String loreKey) {
        ItemStack displayStack = item.getDefaultStack();
        displayStack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable(loreKey)
        )));
        return displayStack;
    }

    private static boolean buyLockpick(PlayerEntity player) {
        if (hasDoorTool(player, WatheItems.LOCKPICK)) {
            player.sendMessage(Text.translatable("shop.error.noellesroles.loose_end.already_has_lockpick"), true);
            return false;
        }

        removeDoorTools(player);
        return ShopEntry.insertStackInFreeSlot(player, WatheItems.LOCKPICK.getDefaultStack());
    }

    private static boolean buyCrowbar(PlayerEntity player) {
        if (hasDoorTool(player, WatheItems.CROWBAR)) {
            player.sendMessage(Text.translatable("shop.error.noellesroles.loose_end.already_has_crowbar"), true);
            return false;
        }

        removeDoorTools(player);
        return ShopEntry.insertStackInFreeSlot(player, WatheItems.CROWBAR.getDefaultStack());
    }

    private static boolean hasDoorTool(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return false;
    }

    private static void removeDoorTools(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isDoorTool(stack)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    private static boolean isDoorTool(ItemStack stack) {
        return stack.isOf(WatheItems.LOCKPICK) || stack.isOf(WatheItems.CROWBAR) || stack.isOf(ModItems.MASTER_KEY);
    }

    public static String getCooldownDenyReason(PlayerEntity player, ShopEntry entry) {
        if (!isLooseEnd(player) || entry == null) {
            return null;
        }

        ItemStack stack = entry.stack();
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();
        if (!isCooldownRestrictedItem(item)) {
            return null;
        }

        ItemCooldownManager cooldownManager = player.getItemCooldownManager();
        return cooldownManager.isCoolingDown(item) ? COOLDOWN_DENY_KEY : null;
    }

    public static String getOwnershipDenyReason(PlayerEntity player, ShopEntry entry) {
        if (!isLooseEnd(player) || entry == null) {
            return null;
        }

        ItemStack stack = entry.stack();
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();
        if (item == WatheItems.LOCKPICK || item == WatheItems.CROWBAR) {
            return null;
        }
        if (item == WatheItems.POISON_VIAL || item == ModItems.HUNTER_TRAP || item == ModItems.DOUBLE_BARREL_SHELL
                || item == ModItems.POISON_GAS_BOMB || item == WatheItems.GRENADE) {
            return null;
        }

        return hasItem(player, item) ? ALREADY_OWNED_DENY_KEY : null;
    }

    private static boolean isCooldownRestrictedItem(Item item) {
        return item == ModItems.REPAIR_TOOL
                || item == ModItems.ANTIDOTE
                || item == ModItems.POISON_NEEDLE
                || item == ModItems.DOUBLE_BARREL_SHOTGUN
                || item == ModItems.TIMED_BOMB
                || item == ModItems.THROWING_AXE
                || item == ModItems.RIOT_SHIELD
                || item == ModItems.PORTABLE_RADAR
                || item == WatheItems.GRENADE
                || item == WatheItems.REVOLVER;
    }

    private static boolean hasItem(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
