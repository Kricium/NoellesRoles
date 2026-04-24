package org.agmas.noellesroles.hallucination;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheSounds;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.ConfigWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bomber.BomberPlayerComponent;
import org.agmas.noellesroles.murdermayhem.FogOfWarMurderMayhemEvent;
import org.agmas.noellesroles.murdermayhem.MurderMayhemWorldComponent;
import org.agmas.noellesroles.packet.HallucinationDummyStateS2CPacket;
import org.agmas.noellesroles.packet.HallucinationSoundS2CPacket;
import org.agmas.noellesroles.util.SpectatorStateHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class HallucinationHelper {
    private HallucinationHelper() {
    }

    public static void tickPlayer(ServerPlayerEntity player, ServerWorld world, GameWorldComponent gameWorld, MurderMayhemWorldComponent murderMayhem) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        if (!GameFunctions.isPlayerPlayingAndAlive(player) || SpectatorStateHelper.isSpectatorLike(player)) {
            if (component.hasAnyHallucination()) {
                component.clearAllHallucinations(false);
            }
            component.tickServerState();
            return;
        }

        component.tickServerState();
        removeNearbyBasicDummies(player, component);
        component.markHigherLevelForRemoval(getLevelForRadius(murderMayhem.getFogRadius()));
        if (!component.canRollNewHallucination()) {
            return;
        }

        int level = getLevelForRadius(murderMayhem.getFogRadius());
        if (level <= 0) {
            component.markRollChecked();
            component.sync();
            return;
        }

        component.markRollChecked();
        float chance = getChanceForRadius(murderMayhem.getFogRadius());
        if (world.getRandom().nextFloat() > chance) {
            component.sync();
            return;
        }

        Optional<HallucinationEffectId> selected = selectCandidate(player, world, gameWorld, component, level);
        if (selected.isEmpty()) {
            component.sync();
            return;
        }

        apply(player, world, gameWorld, component, selected.get());
        component.sync();
    }

    public static List<HallucinationEffectId> getCandidates(PlayerEntity player,
                                                            ServerWorld world,
                                                            GameWorldComponent gameWorld,
                                                            HallucinationPlayerComponent component,
                                                            int level) {
        List<HallucinationEffectId> candidates = new ArrayList<>();
        for (HallucinationEffectId effectId : HallucinationEffectId.values()) {
            if (effectId.level() != level || !component.canApply(effectId)) {
                continue;
            }
            if (effectId == HallucinationEffectId.FAKE_SANITY) {
                if (gameWorld.getRole(player) == null || gameWorld.getRole(player).getMoodType() != dev.doctor4t.wathe.api.Role.MoodType.REAL) {
                    continue;
                }
            }
            if (effectId == HallucinationEffectId.FAKE_TIME && !canPerceiveTimeUi(player)) {
                continue;
            }
            if (effectId == HallucinationEffectId.FAKE_MONEY && !canPerceiveMoneyUi(player)) {
                continue;
            }
            if (effectId == HallucinationEffectId.INSTINCT_MISJUDGE && !gameWorld.canUseKillerFeatures(player)) {
                continue;
            }
            if (effectId == HallucinationEffectId.HIDDEN_UI && chooseUiToHide(component, gameWorld, player).isEmpty()) {
                continue;
            }
            candidates.add(effectId);
        }
        return candidates;
    }

    private static Optional<HallucinationEffectId> selectCandidate(PlayerEntity player,
                                                                  ServerWorld world,
                                                                  GameWorldComponent gameWorld,
                                                                  HallucinationPlayerComponent component,
                                                                  int level) {
        if (level <= 0) {
            return Optional.empty();
        }

        List<HallucinationEffectId> currentCandidates = getCandidates(player, world, gameWorld, component, level);
        if (level == 1) {
            return pickRandom(world, currentCandidates);
        }

        if (currentCandidates.isEmpty()) {
            return selectCandidate(player, world, gameWorld, component, level - 1);
        }

        if (world.getRandom().nextFloat() < 0.7F) {
            return pickRandom(world, currentCandidates);
        }

        return selectCandidate(player, world, gameWorld, component, level - 1)
                .or(() -> pickRandom(world, currentCandidates));
    }

    private static Optional<HallucinationEffectId> pickRandom(ServerWorld world, List<HallucinationEffectId> candidates) {
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(world.getRandom().nextInt(candidates.size())));
    }

    public static void apply(ServerPlayerEntity player,
                             ServerWorld world,
                             GameWorldComponent gameWorld,
                             HallucinationPlayerComponent component,
                             HallucinationEffectId effectId) {
        switch (effectId) {
            case BASIC_DUMMY -> spawnDummy(player, world, component, HallucinationDummyKind.BASIC, false);
            case HIDDEN_BODIES -> component.addOrRefresh(effectId, HallucinationPlayerComponent.HIDDEN_BODIES_TICKS);
            case FAKE_TIME -> component.addFakeTimeSeconds(30);
            case FAKE_MONEY -> component.addFakeMoney(50);
            case FAKE_SANITY -> {
                PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
                int sanityPercent = Math.round(mood.getMood() * 100.0F);
                component.setFakeSanitySnapshot(sanityPercent, getVisibleTaskCount(player));
            }
            case FAKE_SOUND -> {
                component.addOrRefresh(effectId, GameConstants.getInTicks(0, 5));
                RegistrySoundPick pick = RegistrySoundPick.pick(world.getRandom().nextInt(3));
                ServerPlayNetworking.send(player, new HallucinationSoundS2CPacket(pick.soundId));
                component.removeEffect(HallucinationEffectId.FAKE_SOUND);
            }
            case KILLER_DUMMY -> spawnDummy(player, world, component, HallucinationDummyKind.KILLER, true);
            case HIDDEN_PLAYER -> findRandomLiveTarget(player, world, gameWorld, component.getHiddenPlayerUuids())
                    .ifPresent(component::setHiddenPlayer);
            case INSTINCT_MISJUDGE -> findRandomLiveTarget(player, world, gameWorld, component.getInstinctMisjudgeTargets())
                    .ifPresent(target -> component.setInstinctMisjudge(target, world.getRandom().nextBoolean()));
            case SCRAMBLED_SKINS -> component.addOrRefresh(effectId, HallucinationPlayerComponent.SKIN_SCRAMBLE_TICKS);
            case HIDDEN_UI -> chooseUiToHide(component, gameWorld, player).ifPresent(component::hideUi);
        }
    }

    public static boolean tryCleanseFromMedicine(PlayerEntity player) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        boolean success = player.getWorld().getRandom().nextFloat() <= 0.9F;
        if (success) {
            component.clearAllHallucinations(true);
            component.sync();
        }
        return success;
    }

    public static boolean tryCleanseFromSleep(PlayerEntity player) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        component.startSleepCooldown();
        component.setSleepSessionHandled(true);
        boolean success = player.getWorld().getRandom().nextFloat() <= 0.7F;
        if (success) {
            component.clearAllHallucinations(true);
            player.sendMessage(Text.translatable("hallucination.cleanse.success"), true);
            component.sync();
        }
        return success;
    }

    public static void tickSleepRecovery(ServerPlayerEntity player) {
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(player);
        if (!player.isSleeping()) {
            if (component.getSleepRecoveryTicks() > 0) {
                component.resetSleepRecoveryProgress();
                component.sync();
            }
            if (component.isSleepSessionHandled()) {
                component.setSleepSessionHandled(false);
                component.sync();
            }
            return;
        }
        if (component.isSleepSessionHandled()) {
            return;
        }

        PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
        if (hasSleepRecoveryCompleted(player, component, mood)) {
            tryCleanseFromSleep(player);
            player.wakeUp(false, true);
        }
    }

    public static boolean hasSleepCooldown(PlayerEntity player) {
        return HallucinationPlayerComponent.KEY.get(player).getSleepCooldownTicks() > 0;
    }

    public static int getSleepCooldownSeconds(PlayerEntity player) {
        return MathHelper.ceil(HallucinationPlayerComponent.KEY.get(player).getSleepCooldownTicks() / 20.0F);
    }

    public static int getLevelForRadius(int radius) {
        if (radius == 0) {
            return 4;
        }
        if (radius >= 1 && radius <= 4) {
            return 3;
        }
        if (radius >= 5 && radius <= 8) {
            return 2;
        }
        if (radius >= 9 && radius <= 12) {
            return 1;
        }
        return 0;
    }

    public static float getChanceForRadius(int radius) {
        if (radius == 0) {
            return 0.8F;
        }
        if (radius >= 1 && radius <= 4) {
            return 0.6F;
        }
        if (radius >= 5 && radius <= 8) {
            return 0.4F;
        }
        if (radius >= 9 && radius <= 12) {
            return 0.2F;
        }
        return 0.0F;
    }

    public static KillRewardResult computeKillerRewardPreview(PlayerEntity killer, PlayerEntity victim, Identifier deathReason) {
        if (killer == null || victim == null) {
            return KillRewardResult.NONE;
        }
        BomberPlayerComponent bomberComponent = BomberPlayerComponent.KEY.get(victim);
        return KillRewardResolver.resolve(new KillRewardContext(
                victim,
                killer,
                true,
                deathReason == null ? GameConstants.DeathReasons.KNIFE : deathReason,
                bomberComponent.hasBomb() ? bomberComponent.getBomberUuid() : null,
                null,
                null
        ));
    }

    public static boolean isHallucinationTargetHidden(PlayerEntity viewer, PlayerEntity target) {
        if (viewer == null || target == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(viewer);
        return component.hidesPlayer(target.getUuid());
    }

    public static boolean hasInstinctMisjudge(PlayerEntity viewer, PlayerEntity target) {
        if (viewer == null || target == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(viewer).hasInstinctMisjudge(target.getUuid());
    }

    public static boolean isInstinctMisjudgeTreatAsAlly(PlayerEntity viewer, PlayerEntity target) {
        if (viewer == null || target == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(viewer).isInstinctMisjudgeTreatAsAlly(target.getUuid());
    }

    public static boolean shouldHideVoiceFor(ServerPlayerEntity receiver, ServerPlayerEntity speaker) {
        if (receiver == null || speaker == null || receiver == speaker) {
            return false;
        }
        if (SpectatorStateHelper.isSpectatorLike(receiver)) {
            return false;
        }
        return isHallucinationTargetHidden(receiver, speaker);
    }

    public static int getVisibleTaskCount(PlayerEntity player) {
        if (player == null) {
            return 0;
        }
        PlayerMoodComponent mood = PlayerMoodComponent.KEY.get(player);
        int count = 0;
        for (Map.Entry<PlayerMoodComponent.Task, PlayerMoodComponent.TrainTask> entry : mood.tasks.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isFulfilled(player)) {
                count++;
            }
        }
        return count;
    }

    public static Optional<HallucinationDummyState> findNearestBasicDummy(PlayerEntity viewer, double distance) {
        return HallucinationPlayerComponent.KEY.get(viewer).getDummyStates().stream()
                .filter(dummy -> dummy.kind() == HallucinationDummyKind.BASIC)
                .filter(dummy -> dummy.position().squaredDistanceTo(viewer.getPos()) <= distance * distance)
                .min(Comparator.comparingDouble(dummy -> dummy.position().squaredDistanceTo(viewer.getPos())));
    }

    private static void removeNearbyBasicDummies(ServerPlayerEntity player, HallucinationPlayerComponent component) {
        List<UUID> toRemove = component.getDummyStates().stream()
                .filter(dummy -> dummy.kind() == HallucinationDummyKind.BASIC)
                .filter(dummy -> dummy.position().squaredDistanceTo(player.getPos()) <= 9.0D)
                .map(HallucinationDummyState::id)
                .toList();
        if (toRemove.isEmpty()) {
            return;
        }
        toRemove.forEach(component::removeDummy);
        ServerPlayNetworking.send(player, new HallucinationDummyStateS2CPacket(List.of(), toRemove, component.getFakeBodyIds(), component.getFakeBodyDeathReasons()));
        component.sync();
    }

    private static void spawnDummy(ServerPlayerEntity player,
                                   ServerWorld world,
                                   HallucinationPlayerComponent component,
                                   HallucinationDummyKind kind,
                                   boolean collidable) {
        Optional<PlayerEntity> skinTarget = getRandomSkinTarget(world, player);
        UUID skinUuid = skinTarget.map(PlayerEntity::getUuid).orElse(player.getUuid());
        String skinName = skinTarget.map(target -> target.getGameProfile().getName()).orElse(player.getGameProfile().getName());
        Optional<Vec3d> position = findDummyPosition(player, world, component);
        if (position.isEmpty()) {
            return;
        }
        UUID dummyId = UUID.randomUUID();
        UUID rewardReferenceVictimUuid = null;
        if (kind == HallucinationDummyKind.KILLER) {
            PlayerEntity rewardReference = skinTarget.orElse(player);
            rewardReferenceVictimUuid = rewardReference.getUuid();
        }
        float bodyYaw = Math.floorMod(dummyId.hashCode(), 360);
        int localEntityId = Integer.MAX_VALUE - Math.abs(dummyId.hashCode());
        HallucinationDummyState state = new HallucinationDummyState(dummyId, kind, skinUuid, skinName, position.get(), collidable, localEntityId, bodyYaw);
        component.addDummy(state, rewardReferenceVictimUuid);
        if (kind == HallucinationDummyKind.KILLER) {
            component.addOrRefresh(HallucinationEffectId.KILLER_DUMMY, HallucinationPlayerComponent.HIDDEN_PLAYER_TICKS);
        }
        ServerPlayNetworking.send(player, new HallucinationDummyStateS2CPacket(List.of(state), List.of(), component.getFakeBodyIds(), component.getFakeBodyDeathReasons()));
    }

    private static Optional<Vec3d> findDummyPosition(ServerPlayerEntity player, ServerWorld world, HallucinationPlayerComponent component) {
        MurderMayhemWorldComponent murderMayhem = MurderMayhemWorldComponent.KEY.get(world);
        int radius = MathHelper.clamp(murderMayhem.getFogRadius() + 5, 5, FogOfWarMurderMayhemEvent.MAX_FOG_RADIUS + 5);
        BlockPos origin = player.getBlockPos();
        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2.0D / 16.0D) * i;
            int x = origin.getX() + MathHelper.floor(Math.cos(angle) * radius);
            int z = origin.getZ() + MathHelper.floor(Math.sin(angle) * radius);
            for (int dy = -4; dy <= 4; dy++) {
                BlockPos feetPos = new BlockPos(x, origin.getY() + dy, z);
                BlockPos groundPos = feetPos.down();
                if (!world.isAir(feetPos) || !world.isAir(feetPos.up())) {
                    continue;
                }
                if (!world.getBlockState(groundPos).isSolidBlock(world, groundPos)) {
                    continue;
                }
                Vec3d spawnPos = Vec3d.ofBottomCenter(feetPos);
                if (spawnPos.squaredDistanceTo(player.getPos()) < 25.0D) {
                    continue;
                }
                boolean overlapsExistingDummy = component.getDummyStates().stream()
                        .anyMatch(dummy -> dummy.position().squaredDistanceTo(spawnPos) < 6.25D);
                if (overlapsExistingDummy) {
                    continue;
                }
                return Optional.of(spawnPos);
            }
        }
        return Optional.empty();
    }

    private static Optional<PlayerEntity> getRandomSkinTarget(ServerWorld world, PlayerEntity viewer) {
        List<PlayerEntity> players = new ArrayList<>(world.getPlayers().stream()
                .filter(GameFunctions::isPlayerPlayingAndAlive)
                .filter(candidate -> !candidate.getUuid().equals(viewer.getUuid()))
                .toList());
        if (players.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(players.get(world.getRandom().nextInt(players.size())));
    }

    private static Optional<UUID> findRandomLiveTarget(PlayerEntity player,
                                                       ServerWorld world,
                                                       GameWorldComponent gameWorld,
                                                       java.util.Collection<UUID> blockedUuids) {
        List<UUID> candidates = new ArrayList<>();
        for (UUID uuid : gameWorld.getAllPlayers()) {
            if (uuid.equals(player.getUuid()) || (blockedUuids != null && blockedUuids.contains(uuid)) || gameWorld.isPlayerDead(uuid)) {
                continue;
            }
            if (world.getPlayerByUuid(uuid) == null) {
                continue;
            }
            candidates.add(uuid);
        }
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(world.getRandom().nextInt(candidates.size())));
    }

    private static boolean canPerceiveTimeUi(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        ConfigWorldComponent configWorld = ConfigWorldComponent.KEY.get(player.getWorld());
        return gameWorld != null
                && configWorld != null
                && GameFunctions.isPlayerPlayingAndAlive(player)
                && !SpectatorStateHelper.isSpectatorLike(player)
                && gameWorld.hasAnyRole(player)
                && configWorld.showFogRadiusHud;
    }

    private static boolean canPerceiveMoneyUi(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        return dev.doctor4t.wathe.api.event.CanSeeMoney.EVENT.invoker().canSee(player)
                == dev.doctor4t.wathe.api.event.CanSeeMoney.Result.ALLOW;
    }

    private static Optional<HallucinationUiSlot> chooseUiToHide(HallucinationPlayerComponent component, GameWorldComponent gameWorld, PlayerEntity player) {
        List<HallucinationUiSlot> candidates = new ArrayList<>();
        if (!component.isUiHidden(HallucinationUiSlot.TIME) && canPerceiveTimeUi(player)) {
            candidates.add(HallucinationUiSlot.TIME);
        }
        if (!component.isUiHidden(HallucinationUiSlot.MONEY) && canPerceiveMoneyUi(player)) {
            candidates.add(HallucinationUiSlot.MONEY);
        }
        if (!component.isUiHidden(HallucinationUiSlot.SKILL_HINT) && canPerceiveSkillHintUi(player, gameWorld)) {
            candidates.add(HallucinationUiSlot.SKILL_HINT);
        }
        if (!component.isUiHidden(HallucinationUiSlot.SANITY) && canPerceiveSanityUi(player, gameWorld)) {
            candidates.add(HallucinationUiSlot.SANITY);
        }
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(player.getWorld().getRandom().nextInt(candidates.size())));
    }

    private static boolean canPerceiveSanityUi(PlayerEntity player, GameWorldComponent gameWorld) {
        if (player == null || gameWorld == null || !gameWorld.hasAnyRole(player)) {
            return false;
        }
        return gameWorld.getRole(player) != null && gameWorld.getRole(player).getMoodType() != dev.doctor4t.wathe.api.Role.MoodType.NONE;
    }

    private static boolean canPerceiveSkillHintUi(PlayerEntity player, GameWorldComponent gameWorld) {
        if (player == null || gameWorld == null || !gameWorld.hasAnyRole(player)) {
            return false;
        }
        if (!GameFunctions.isPlayerPlayingAndAlive(player) || SpectatorStateHelper.isSpectatorLike(player)) {
            return false;
        }
        var role = gameWorld.getRole(player);
        if (role == null) {
            return false;
        }
        if (AbilityPlayerComponent.KEY.get(player).getCooldown() > 0) {
            return true;
        }
        return role == Noellesroles.ASSASSIN
                || role == Noellesroles.COMMANDER
                || role == Noellesroles.CRIMINAL_REASONER
                || role == Noellesroles.ORTHOPEDIST
                || role == Noellesroles.PATHOGEN
                || role == Noellesroles.PHANTOM
                || role == Noellesroles.REPORTER
                || role == Noellesroles.SILENCER
                || role == Noellesroles.SWAPPER
                || role == Noellesroles.TAOTIE;
    }

    private static boolean gameAllowsMoneyHud(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        return gameWorld.isRole(player, Noellesroles.RECALLER);
    }

    private static boolean hasSleepRecoveryCompleted(PlayerEntity player,
                                                     HallucinationPlayerComponent component,
                                                     PlayerMoodComponent mood) {
        if (hasSleepTaskCompleted(player, mood)) {
            return true;
        }
        boolean complete = component.tickSleepRecoveryProgress();
        component.sync();
        return complete;
    }

    private static boolean hasSleepTaskCompleted(PlayerEntity player, PlayerMoodComponent mood) {
        if (mood == null) {
            return false;
        }
        PlayerMoodComponent.TrainTask task = mood.tasks.get(PlayerMoodComponent.Task.SLEEP);
        return task != null && task.isFulfilled(player);
    }

    private enum RegistrySoundPick {
        LOCK(WatheSounds.ITEM_LOCKPICK_DOOR.getId().toString()),
        GUN(WatheSounds.ITEM_REVOLVER_SHOOT.getId().toString()),
        KNIFE(WatheSounds.ITEM_KNIFE_PREPARE.getId().toString());

        private final String soundId;

        RegistrySoundPick(String soundId) {
            this.soundId = soundId;
        }

        static RegistrySoundPick pick(int index) {
            return values()[MathHelper.clamp(index, 0, values().length - 1)];
        }
    }
}
