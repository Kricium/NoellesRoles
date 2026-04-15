package org.agmas.noellesroles.client.mixin.coroner;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import org.agmas.noellesroles.util.BodyTargetHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(RoleNameRenderer.class)
public class RoleNameRendererBodyTargetMixin {

    @WrapOperation(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 1
            )
    )
    private static HitResult noellesroles$useDedicatedBodyRaycast(Entity entity,
                                                                  Predicate<Entity> predicate,
                                                                  double maxDistance,
                                                                  Operation<HitResult> original,
                                                                  net.minecraft.client.font.TextRenderer renderer,
                                                                  ClientPlayerEntity player,
                                                                  net.minecraft.client.gui.DrawContext context,
                                                                  RenderTickCounter tickCounter) {
        return BodyTargetHelper.raycastBody(player, maxDistance, body -> true);
    }
}
