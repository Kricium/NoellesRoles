package org.agmas.noellesroles.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 工程师维修工具
 * 可修复被撬的门、上锁/解锁门，3分钟冷却
 * 实际交互逻辑通过 DoorInteraction.EVENT 在 Noellesroles.registerEvents() 中处理
 */
public class RepairToolItem extends Item {
    public static final int COOLDOWN_TICKS = 3600; // 3 minutes

    public RepairToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.repair_tool.tooltip"));
    }
}
