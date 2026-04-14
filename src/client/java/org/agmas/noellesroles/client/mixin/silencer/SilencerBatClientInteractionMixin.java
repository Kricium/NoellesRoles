package org.agmas.noellesroles.client.mixin.silencer;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.silencer.SilencerBatClientSoundGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class SilencerBatClientInteractionMixin {

    /**
     * 客户端在发出攻击包后会本地再执行一次 player.attack(target) 作为预测，
     * 这会带出原版的攻击音效。静语者球棒命中玩家时跳过这一步，只保留服务端
     * 的 Wathe 特色击杀处理。
     */
    @Redirect(
            method = "attackEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"
            )
    )
    private void noellesroles$skipSilencerBatClientAttack(PlayerEntity player, Entity target) {
        if (target instanceof PlayerEntity
                && player.getMainHandStack().isOf(WatheItems.BAT)
                && player.getAttackCooldownProgress(0.5F) >= 1.0F) {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            if (gameWorld.isRole(player, Noellesroles.SILENCER)) {
                SilencerBatClientSoundGate.arm();
                return;
            }
        }

        player.attack(target);
    }
}
