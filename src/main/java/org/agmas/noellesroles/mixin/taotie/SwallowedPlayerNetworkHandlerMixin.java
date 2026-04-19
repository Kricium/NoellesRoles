package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.noellesroles.util.SwallowedInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class SwallowedPlayerNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private boolean floating;

    @Shadow
    private int floatingTicks;

    @Shadow
    private boolean vehicleFloating;

    @Shadow
    private int vehicleFloatingTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void noellesroles$clearSwallowedFloatingState(CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            this.floating = false;
            this.floatingTicks = 0;
            this.vehicleFloating = false;
            this.vehicleFloatingTicks = 0;
        }
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            this.floating = false;
            this.floatingTicks = 0;
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedSelectedSlot(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onHandSwing", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedHandSwing(HandSwingC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
            return;
        }

        Entity target = packet.getEntity(this.player.getServerWorld());
        if (SwallowedInteractionHelper.blocksTarget(target)) {
            ci.cancel();
        }
    }

    @Inject(method = "onClickSlot", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onCreativeInventoryAction", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedCreativeInventory(CreativeInventoryActionC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onClientCommand", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedClientCommand(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if (SwallowedInteractionHelper.blocksActor(this.player)) {
            ci.cancel();
        }
    }
}
