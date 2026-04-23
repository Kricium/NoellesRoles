package org.agmas.noellesroles.client.mixin.murdermayhem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.RoleNameRenderer")
public class HallucinationRoleNameRendererMixin {

    @WrapOperation(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 0
            ),
            remap = false
    )
    private static HitResult noellesroles$hideHallucinatedRoleHudTarget(
            Entity entity,
            Predicate<Entity> predicate,
            double maxDistance,
            Operation<HitResult> original,
            net.minecraft.client.font.TextRenderer renderer,
            ClientPlayerEntity player,
            net.minecraft.client.gui.DrawContext context,
            RenderTickCounter tickCounter
    ) {
        return ProjectileUtil.getCollision(entity, candidate ->
                predicate.test(candidate)
                        && (!(candidate instanceof PlayerEntity target)
                        || !HallucinationClientVisibilityHelper.shouldHidePlayer(player, target)), maxDistance);
    }
}
