package org.agmas.noellesroles.client.hallucination;

import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheEntities;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.hallucination.HallucinationDummyKind;
import org.agmas.noellesroles.hallucination.HallucinationDummyState;
import org.agmas.noellesroles.hallucination.HallucinationPlayerComponent;
import org.agmas.noellesroles.packet.HallucinationDummyHitC2SPacket;
import org.agmas.noellesroles.packet.HallucinationDummyUseAction;
import org.agmas.noellesroles.packet.HallucinationDummyUseC2SPacket;
import dev.doctor4t.wathe.index.WatheSounds;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ClientHallucinationState {
    private static final Map<UUID, HallucinationDummyState> DUMMIES = new HashMap<>();
    private static final Map<UUID, OtherClientPlayerEntity> DUMMY_ENTITIES = new HashMap<>();
    private static final Map<UUID, PlayerBodyEntity> FAKE_BODIES = new HashMap<>();
    private static final Map<UUID, Identifier> FAKE_BODY_DEATH_REASONS = new HashMap<>();
    private static final Map<UUID, HurtDummyMotion> HURT_DUMMY_MOTIONS = new HashMap<>();

    private ClientHallucinationState() {
    }

    public static void reset() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            DUMMY_ENTITIES.values().forEach(entity -> client.world.removeEntity(entity.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED));
            FAKE_BODIES.values().forEach(entity -> client.world.removeEntity(entity.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED));
        }
        DUMMIES.clear();
        DUMMY_ENTITIES.clear();
        FAKE_BODIES.clear();
        FAKE_BODY_DEATH_REASONS.clear();
        HURT_DUMMY_MOTIONS.clear();
    }

    public static void applyDummySync(MinecraftClient client,
                                      List<HallucinationDummyState> added,
                                      List<UUID> removed,
                                      List<UUID> fakeBodies,
                                      Map<UUID, Identifier> fakeBodyDeathReasons) {
        ClientWorld world = client.world;
        if (world == null) {
            reset();
            return;
        }

        FAKE_BODY_DEATH_REASONS.putAll(fakeBodyDeathReasons);

        for (UUID uuid : removed) {
            HallucinationDummyState removedState = DUMMIES.remove(uuid);
            OtherClientPlayerEntity removedEntity = DUMMY_ENTITIES.remove(uuid);
            if (removedEntity != null) {
                world.removeEntity(removedEntity.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            }
            HURT_DUMMY_MOTIONS.remove(uuid);
            if (removedState != null && fakeBodies.contains(uuid)) {
                PlayerBodyEntity body = createFakeBodyEntity(world, removedState, getFakeBodyDeathReason(uuid));
                registerEntity(world, body);
                FAKE_BODIES.put(uuid, body);
            } else {
                PlayerBodyEntity body = FAKE_BODIES.remove(uuid);
                FAKE_BODY_DEATH_REASONS.remove(uuid);
                if (body != null) {
                    world.removeEntity(body.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
                }
            }
        }
        for (HallucinationDummyState state : added) {
            DUMMIES.put(state.id(), state);
            OtherClientPlayerEntity entity = DUMMY_ENTITIES.get(state.id());
            if (entity == null) {
                entity = createDummyEntity(world, state);
                DUMMY_ENTITIES.put(state.id(), entity);
            }
            registerEntity(world, entity);
            applyState(entity, state, client);
        }
        FAKE_BODIES.entrySet().removeIf(entry -> {
            if (fakeBodies.contains(entry.getKey())) {
                return false;
            }
            world.removeEntity(entry.getValue().getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            FAKE_BODY_DEATH_REASONS.remove(entry.getKey());
            return true;
        });
    }

    public static void playSound(MinecraftClient client, String soundId) {
        if (client.world == null || client.player == null) {
            return;
        }
        Identifier id = Identifier.tryParse(soundId);
        if (id == null) {
            return;
        }
        var optional = net.minecraft.registry.Registries.SOUND_EVENT.getEntry(id);
        optional.ifPresent(entry -> client.world.playSound(
                client.player,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ(),
                entry,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F,
                client.world.random.nextLong()
        ));
    }

    public static boolean isFakeBody(UUID bodyUuid) {
        return bodyUuid != null && FAKE_BODIES.containsKey(bodyUuid);
    }

    public static boolean hasDummyForSkin(UUID playerUuid) {
        return playerUuid != null && DUMMIES.values().stream().anyMatch(dummy -> dummy.skinUuid().equals(playerUuid));
    }

    public static boolean hasKillerDummyForSkin(UUID playerUuid) {
        return playerUuid != null && DUMMIES.values().stream()
                .anyMatch(dummy -> dummy.kind() == HallucinationDummyKind.KILLER && dummy.skinUuid().equals(playerUuid));
    }

    public static @Nullable OtherClientPlayerEntity getDummyEntity(UUID dummyId) {
        return DUMMY_ENTITIES.get(dummyId);
    }

    public static @Nullable OtherClientPlayerEntity getDummyEntityForPlayer(UUID playerUuid) {
        if (playerUuid == null) {
            return null;
        }
        for (Map.Entry<UUID, HallucinationDummyState> entry : DUMMIES.entrySet()) {
            if (entry.getValue().skinUuid().equals(playerUuid)) {
                return DUMMY_ENTITIES.get(entry.getKey());
            }
        }
        return null;
    }

    public static @Nullable OtherClientPlayerEntity getKillerDummyEntityForPlayer(UUID playerUuid) {
        if (playerUuid == null) {
            return null;
        }
        for (Map.Entry<UUID, HallucinationDummyState> entry : DUMMIES.entrySet()) {
            HallucinationDummyState state = entry.getValue();
            if (state.kind() == HallucinationDummyKind.KILLER && state.skinUuid().equals(playerUuid)) {
                return DUMMY_ENTITIES.get(entry.getKey());
            }
        }
        return null;
    }

    public static List<HallucinationDummyState> getDummies() {
        return List.copyOf(DUMMIES.values());
    }

    public static boolean shouldSuppressDirectPlayerHighlight(UUID playerUuid) {
        return getDummyEntityForPlayer(playerUuid) != null;
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            reset();
            return;
        }
        syncDummiesFromComponent(client);
    }

    public static void renderFakeBodies(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            reset();
            return;
        }
        syncDummiesFromComponent(client);
        syncFakeBodiesFromComponent(client);
    }

    public static boolean tryHitDummy(MinecraftClient client) {
        return tryHitDummy(client, 4.0D, null);
    }

    public static boolean tryHitDummyEntity(MinecraftClient client, Entity entity, @Nullable Identifier deathReason) {
        UUID dummyId = getDummyId(entity);
        if (dummyId == null) {
            return false;
        }

        if (client.player == null || client.world == null) {
            return false;
        }

        ItemStack heldStack = client.player.getMainHandStack();
        if (heldStack.isEmpty()) {
            return false;
        }
        if (client.player.getItemCooldownManager().isCoolingDown(heldStack.getItem())) {
            return false;
        }

        Identifier resolvedDeathReason = deathReason != null ? deathReason : resolveHeldItemDeathReason(heldStack);
        return killDummy(client, dummyId, resolvedDeathReason);
    }

    public static boolean tryHitDummy(MinecraftClient client, double range, @Nullable Identifier deathReason) {
        if (client.player == null || client.world == null) {
            return false;
        }
        ItemStack heldStack = client.player.getMainHandStack();
        if (heldStack.isEmpty() || !GameConstants.ITEM_COOLDOWNS.containsKey(heldStack.getItem())) {
            return false;
        }
        if (client.player.getItemCooldownManager().isCoolingDown(heldStack.getItem())) {
            return false;
        }
        Vec3d eyePos = client.player.getEyePos();
        Vec3d look = client.player.getRotationVec(1.0F);
        Vec3d endPos = eyePos.add(look.multiply(range));

        UUID hitId = findHitDummyId(eyePos, endPos);
        if (hitId == null) {
            return false;
        }

        Identifier resolvedDeathReason = deathReason != null ? deathReason : resolveHeldItemDeathReason(heldStack);
        return killDummy(client, hitId, resolvedDeathReason);
    }

    public static boolean isDummyEntity(net.minecraft.entity.Entity entity) {
        return entity instanceof OtherClientPlayerEntity other && DUMMY_ENTITIES.containsValue(other);
    }

    public static boolean dummyHasBomb(@Nullable UUID dummyId) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || dummyId == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(client.player).getDummyBombState(dummyId).isPresent();
    }

    public static boolean dummyIsPoisoned(@Nullable UUID dummyId) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || dummyId == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(client.player).getDummyPoisonState(dummyId)
                .map(state -> state.poisonTicks() > 0)
                .orElse(false);
    }

    public static boolean isManagedDummyEntity(net.minecraft.entity.Entity entity) {
        return entity instanceof OtherClientPlayerEntity other && DUMMY_ENTITIES.containsValue(other)
                || entity instanceof PlayerBodyEntity body && FAKE_BODIES.containsValue(body);
    }

    public static @Nullable UUID getCrosshairDummyId(MinecraftClient client) {
        if (client == null || !(client.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult entityHitResult)) {
            return null;
        }
        return getDummyId(entityHitResult.getEntity());
    }

    public static boolean tryUseDummy(MinecraftClient client, HallucinationDummyUseAction action) {
        UUID dummyId = getCrosshairDummyId(client);
        if (dummyId == null) {
            return false;
        }
        ClientPlayNetworking.send(new HallucinationDummyUseC2SPacket(dummyId, action));
        return true;
    }

    public static boolean tryUseDummyEntity(Entity entity, HallucinationDummyUseAction action) {
        UUID dummyId = getDummyId(entity);
        if (dummyId == null) {
            return false;
        }
        ClientPlayNetworking.send(new HallucinationDummyUseC2SPacket(dummyId, action));
        return true;
    }

    public static boolean tryHurtDummyEntity(MinecraftClient client, Entity entity) {
        UUID dummyId = getDummyId(entity);
        if (dummyId == null) {
            return false;
        }
        ClientPlayNetworking.send(new HallucinationDummyUseC2SPacket(dummyId, HallucinationDummyUseAction.MELEE_SHOVE));
        OtherClientPlayerEntity dummyEntity = DUMMY_ENTITIES.get(dummyId);
        if (dummyEntity == null) {
            return false;
        }
        dummyEntity.timeUntilRegen = 10;
        dummyEntity.hurtTime = 10;
        dummyEntity.maxHurtTime = 10;
        dummyEntity.handSwinging = true;
        playLocalDummyHurtSound(client);
        if (client.player != null) {
            double strength = playerShoveStrength(client.player);
            double dx = dummyEntity.getX() - client.player.getX();
            double dz = dummyEntity.getZ() - client.player.getZ();
            if (dx * dx + dz * dz < 1.0E-4D) {
                dx = -Math.sin(Math.toRadians(client.player.getYaw()));
                dz = Math.cos(Math.toRadians(client.player.getYaw()));
            }
            HURT_DUMMY_MOTIONS.put(dummyId, new HurtDummyMotion(dx, dz, strength, 6));
        }
        return true;
    }

    private static double playerShoveStrength(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        return stack.isOf(ModItems.RIOT_SHIELD) ? 0.75D : 0.45D;
    }

    private static void applyState(OtherClientPlayerEntity entity, HallucinationDummyState state, MinecraftClient client) {
        Vec3d pos = applyHurtMotion(state);
        entity.setPosition(pos.x, pos.y, pos.z);
        entity.prevX = pos.x;
        entity.prevY = pos.y;
        entity.prevZ = pos.z;
        float yaw = state.bodyYaw();
        entity.setYaw(yaw);
        entity.prevYaw = yaw;
        entity.setHeadYaw(yaw);
        entity.prevHeadYaw = yaw;
        entity.bodyYaw = yaw;
        entity.prevBodyYaw = yaw;
        entity.noClip = !state.collidable();
        entity.setPose(EntityPose.STANDING);
        entity.setOnGround(true);
        entity.setSneaking(false);
        entity.handSwinging = false;
        applyEquipment(entity, state, client);
    }

    private static Vec3d applyHurtMotion(HallucinationDummyState state) {
        HurtDummyMotion motion = HURT_DUMMY_MOTIONS.get(state.id());
        if (motion == null || motion.remainingTicks() <= 0) {
            HURT_DUMMY_MOTIONS.remove(state.id());
            return state.position();
        }

        double length = Math.sqrt(motion.x() * motion.x() + motion.z() * motion.z());
        double normX = length > 1.0E-4D ? motion.x() / length : 0.0D;
        double normZ = length > 1.0E-4D ? motion.z() / length : 0.0D;
        double stepStrength = motion.strength() / 6.0D;
        Vec3d offset = new Vec3d(normX * stepStrength, 0.0D, normZ * stepStrength);
        HURT_DUMMY_MOTIONS.put(state.id(), new HurtDummyMotion(motion.x(), motion.z(), motion.strength(), motion.remainingTicks() - 1));
        return state.position().add(offset);
    }

    private static void applyEquipment(OtherClientPlayerEntity entity, HallucinationDummyState state, MinecraftClient client) {
        if (client.world == null) {
            return;
        }
        PlayerEntity skinTarget = client.world.getPlayerByUuid(state.skinUuid());
        if (skinTarget == null) {
            entity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            entity.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            entity.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
            entity.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
            entity.equipStack(EquipmentSlot.LEGS, ItemStack.EMPTY);
            entity.equipStack(EquipmentSlot.FEET, ItemStack.EMPTY);
            return;
        }

        entity.equipStack(EquipmentSlot.MAINHAND, skinTarget.getMainHandStack().copy());
        entity.equipStack(EquipmentSlot.OFFHAND, skinTarget.getOffHandStack().copy());
        entity.equipStack(EquipmentSlot.HEAD, skinTarget.getEquippedStack(EquipmentSlot.HEAD).copy());
        entity.equipStack(EquipmentSlot.CHEST, skinTarget.getEquippedStack(EquipmentSlot.CHEST).copy());
        entity.equipStack(EquipmentSlot.LEGS, skinTarget.getEquippedStack(EquipmentSlot.LEGS).copy());
        entity.equipStack(EquipmentSlot.FEET, skinTarget.getEquippedStack(EquipmentSlot.FEET).copy());
        if (skinTarget.isUsingItem()) {
            entity.setCurrentHand(skinTarget.getActiveHand());
        } else {
            entity.clearActiveItem();
        }
    }

    private static void syncFakeBodiesFromComponent(MinecraftClient client) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(client.player);
        Set<UUID> validBodyIds = Set.copyOf(component.getFakeBodyIds());
        FAKE_BODIES.entrySet().removeIf(entry -> {
            if (validBodyIds.contains(entry.getKey())) {
                return false;
            }
            FAKE_BODY_DEATH_REASONS.remove(entry.getKey());
            client.world.removeEntity(entry.getValue().getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            return true;
        });
    }

    private static void syncDummiesFromComponent(MinecraftClient client) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(client.player);
        Map<UUID, HallucinationDummyState> validStates = new HashMap<>();
        for (HallucinationDummyState state : component.getDummyStates()) {
            validStates.put(state.id(), state);
        }
        Set<UUID> fakeBodyIds = Set.copyOf(component.getFakeBodyIds());
        DUMMY_ENTITIES.entrySet().removeIf(entry -> {
            if (validStates.containsKey(entry.getKey())) {
                return false;
            }
            HallucinationDummyState removedState = DUMMIES.get(entry.getKey());
            client.world.removeEntity(entry.getValue().getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            if (removedState != null && fakeBodyIds.contains(entry.getKey()) && !FAKE_BODIES.containsKey(entry.getKey())) {
                Identifier deathReason = component.getFakeBodyDeathReason(entry.getKey());
                FAKE_BODY_DEATH_REASONS.put(entry.getKey(), deathReason);
                PlayerBodyEntity body = createFakeBodyEntity(client.world, removedState, deathReason);
                registerEntity(client.world, body);
                FAKE_BODIES.put(entry.getKey(), body);
            }
            return true;
        });
        DUMMIES.keySet().removeIf(uuid -> !validStates.containsKey(uuid));
        validStates.forEach((uuid, state) -> {
            DUMMIES.put(uuid, state);
            OtherClientPlayerEntity entity = DUMMY_ENTITIES.get(uuid);
            if (entity == null) {
                entity = createDummyEntity(client.world, state);
                DUMMY_ENTITIES.put(uuid, entity);
            }
            registerEntity(client.world, entity);
            applyState(entity, state, client);
        });
    }

    private static Box getDummyBox(Vec3d pos) {
        return new Box(
                pos.x - 0.3D,
                pos.y,
                pos.z - 0.3D,
                pos.x + 0.3D,
                pos.y + 1.8D,
                pos.z + 0.3D
        );
    }

    private static void playLocalDummyHurtSound(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }
        client.world.playSound(
                client.player,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ(),
                SoundEvents.ENTITY_PLAYER_HURT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F,
                client.world.random.nextLong()
        );
    }

    private static OtherClientPlayerEntity createDummyEntity(ClientWorld world, HallucinationDummyState state) {
        OtherClientPlayerEntity entity = new OtherClientPlayerEntity(
                world,
                new com.mojang.authlib.GameProfile(state.skinUuid(), state.skinName())
        );
        entity.setId(state.localEntityId());
        entity.setUuid(state.id());
        applyStaticDummyState(entity, state);
        entity.setHealth(20.0F);
        return entity;
    }

    private static PlayerBodyEntity createFakeBodyEntity(ClientWorld world,
                                                         HallucinationDummyState state,
                                                         Identifier deathReason) {
        PlayerBodyEntity body = new PlayerBodyEntity(WatheEntities.PLAYER_BODY, world);
        body.setId(state.localEntityId());
        body.setUuid(state.id());
        body.setPlayerUuid(state.skinUuid());
        body.setDeathReason(deathReason);
        body.setDeathGameTime(world.getTime());
        body.setPosition(state.position());
        body.prevX = state.position().x;
        body.prevY = state.position().y;
        body.prevZ = state.position().z;
        body.setYaw(state.bodyYaw());
        body.prevYaw = state.bodyYaw();
        body.setHeadYaw(state.bodyYaw());
        body.prevHeadYaw = state.bodyYaw();
        body.bodyYaw = state.bodyYaw();
        body.prevBodyYaw = state.bodyYaw();
        body.setPose(EntityPose.STANDING);
        body.setHealth(20.0F);
        return body;
    }

    private static void applyStaticDummyState(OtherClientPlayerEntity entity, HallucinationDummyState state) {
        Vec3d pos = state.position();
        entity.setPosition(pos.x, pos.y, pos.z);
        entity.prevX = pos.x;
        entity.prevY = pos.y;
        entity.prevZ = pos.z;
        float yaw = state.bodyYaw();
        entity.setYaw(yaw);
        entity.prevYaw = yaw;
        entity.setHeadYaw(yaw);
        entity.prevHeadYaw = yaw;
        entity.bodyYaw = yaw;
        entity.prevBodyYaw = yaw;
        entity.noClip = !state.collidable();
        entity.setPose(EntityPose.STANDING);
        entity.setOnGround(true);
    }

    private static void registerEntity(ClientWorld world, net.minecraft.entity.Entity entity) {
        if (world.getEntityById(entity.getId()) != entity) {
            world.removeEntity(entity.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            world.addEntity(entity);
        }
    }

    private static @Nullable UUID findHitDummyId(Vec3d eyePos, Vec3d endPos) {
        UUID hitId = null;
        double hitDistance = Double.MAX_VALUE;
        for (Map.Entry<UUID, HallucinationDummyState> entry : DUMMIES.entrySet()) {
            HallucinationDummyState state = entry.getValue();
            if (state.kind() != HallucinationDummyKind.KILLER) {
                continue;
            }
            Box box = getDummyBox(state.position());
            var optional = box.raycast(eyePos, endPos);
            if (optional.isEmpty()) {
                continue;
            }
            double distance = eyePos.squaredDistanceTo(optional.get());
            if (distance < hitDistance) {
                hitDistance = distance;
                hitId = entry.getKey();
            }
        }
        return hitId;
    }

    private static Identifier resolveHeldItemDeathReason(ItemStack heldStack) {
        Identifier heldItemId = net.minecraft.registry.Registries.ITEM.getId(heldStack.getItem());
        if (heldItemId != null) {
            String path = heldItemId.getPath();
            if (path.contains("gun") || path.contains("revolver")) {
                return GameConstants.DeathReasons.GUN;
            }
            if (path.contains("bat")) {
                return GameConstants.DeathReasons.BAT;
            }
        }
        return GameConstants.DeathReasons.KNIFE;
    }

    private static Identifier getFakeBodyDeathReason(UUID dummyId) {
        return FAKE_BODY_DEATH_REASONS.getOrDefault(dummyId, GameConstants.DeathReasons.KNIFE);
    }

    private static void playLocalDummyKillSound(MinecraftClient client, Identifier deathReason) {
        if (client.world == null || client.player == null) {
            return;
        }
        SoundEvent sound = WatheSounds.ITEM_KNIFE_STAB;
        if (GameConstants.DeathReasons.GUN.equals(deathReason)) {
            sound = WatheSounds.ITEM_REVOLVER_SHOOT;
        } else if (GameConstants.DeathReasons.BAT.equals(deathReason)) {
            sound = WatheSounds.ITEM_BAT_HIT;
        }
        client.world.playSound(
                client.player,
                client.player.getX(),
                client.player.getY(),
                client.player.getZ(),
                sound,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F,
                client.world.random.nextLong()
        );
    }

    public static @Nullable UUID getDummyId(Entity entity) {
        if (!(entity instanceof OtherClientPlayerEntity other)) {
            return null;
        }
        for (Map.Entry<UUID, OtherClientPlayerEntity> entry : DUMMY_ENTITIES.entrySet()) {
            if (entry.getValue() == other) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isNonlethalDummyMelee(ItemStack stack) {
        return stack.isOf(WatheItems.KNIFE) || stack.isOf(ModItems.POISON_NEEDLE);
    }

    public static @Nullable HallucinationDummyUseAction resolveDummyUseAction(ItemStack stack, @Nullable PlayerEntity player) {
        if (stack.isOf(ModItems.POISON_NEEDLE)) {
            return HallucinationDummyUseAction.POISON_NEEDLE_USE;
        }
        if (stack.isOf(ModItems.CATALYST)) {
            return HallucinationDummyUseAction.CATALYST_USE;
        }
        if (stack.isOf(ModItems.ANTIDOTE)) {
            return HallucinationDummyUseAction.ANTIDOTE_USE;
        }
        if (stack.isOf(ModItems.TIMED_BOMB) && player != null) {
            return org.agmas.noellesroles.bomber.BomberPlayerComponent.KEY.get(player).isBeeping()
                    ? HallucinationDummyUseAction.TIMED_BOMB_TRANSFER
                    : HallucinationDummyUseAction.TIMED_BOMB_PLACE;
        }
        return null;
    }

    private static boolean killDummy(MinecraftClient client, UUID dummyId, Identifier deathReason) {
        if (client.player == null || client.world == null) {
            return false;
        }
        ClientPlayNetworking.send(new HallucinationDummyHitC2SPacket(dummyId, deathReason));
        playLocalDummyKillSound(client, deathReason);
        HallucinationDummyState state = DUMMIES.remove(dummyId);
        OtherClientPlayerEntity entity = DUMMY_ENTITIES.remove(dummyId);
        if (state != null && entity != null) {
            client.world.removeEntity(entity.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            FAKE_BODY_DEATH_REASONS.put(dummyId, deathReason);
            PlayerBodyEntity body = createFakeBodyEntity(client.world, state, deathReason);
            registerEntity(client.world, body);
            FAKE_BODIES.put(dummyId, body);
        }
        return true;
    }

    private record HurtDummyMotion(double x, double z, double strength, int remainingTicks) {
    }
}
