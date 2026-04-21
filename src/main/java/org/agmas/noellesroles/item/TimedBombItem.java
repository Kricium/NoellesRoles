package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.agmas.noellesroles.bomber.BomberPlayerComponent;

import java.util.List;

/**
 * 定时炸弹物品
 * - 炸弹客使用：放置炸弹到目标玩家身上
 * - 携带者使用：传递炸弹给目标玩家
 */
public class TimedBombItem extends Item {
    public TimedBombItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.noellesroles.timed_bomb.tooltip"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();

        // 只处理玩家目标
        if (!(entity instanceof PlayerEntity target)) {
            return ActionResult.PASS;
        }

        // 服务端处理
        if (!world.isClient) {

            if(user.getItemCooldownManager().isCoolingDown(this)) {
                return ActionResult.PASS;
            }

            if (!GameFunctions.isPlayerAliveAndSurvival(target)) {
                return ActionResult.PASS;
            }

            BomberPlayerComponent userComponent = BomberPlayerComponent.KEY.get(user);
            BomberPlayerComponent targetComponent = BomberPlayerComponent.KEY.get(target);

            // 这确保即使是炸弹客拿到炸弹后也能正常传递
            if (userComponent.isBeeping()) {
                if (userComponent.canTransfer(target)) {
                    userComponent.transferBomb(target);
                    return ActionResult.CONSUME; // 成功但不挥手
                }
                return ActionResult.PASS;
            }

            // 任何持有定时炸弹的玩家都可以安装；滴滴阶段仍按原逻辑传递
            if (targetComponent.hasBomb()) {
                return ActionResult.PASS;
            }
            targetComponent.placeBomb(user);
            if (GameWorldComponent.KEY.get(world).isRole(user, WatheRoles.LOOSE_END)) {
                user.getItemCooldownManager().set(this, 20 * 30);
            }
            stack.decrementUnlessCreative(1, user);
            return ActionResult.CONSUME; // 成功但不挥手
        }

        return ActionResult.PASS;
    }
}
