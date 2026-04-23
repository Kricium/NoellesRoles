package org.agmas.noellesroles.hallucination;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class HallucinationDummyInteractionHelper {
    private static final double DUMMY_HALF_WIDTH = 0.3D;
    private static final double DUMMY_HEIGHT = 1.8D;

    private HallucinationDummyInteractionHelper() {
    }

    public static Optional<UUID> getDummyId(Entity entity, PlayerEntity viewer) {
        if (entity == null || viewer == null) {
            return Optional.empty();
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(viewer);
        return component.findDummyByLocalEntityId(entity.getId()).map(HallucinationDummyState::id);
    }

    public static Optional<UUID> getDummyIdByEntityId(PlayerEntity viewer, int entityId) {
        if (viewer == null) {
            return Optional.empty();
        }
        return HallucinationPlayerComponent.KEY.get(viewer)
                .findDummyByLocalEntityId(entityId)
                .map(HallucinationDummyState::id);
    }

    public static boolean tryKillDummy(PlayerEntity viewer, int entityId, Identifier deathReason, boolean applyRewards) {
        if (viewer == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(viewer);
        Optional<HallucinationDummyState> dummy = component.findDummyByLocalEntityId(entityId);
        if (dummy.isEmpty()) {
            return false;
        }
        return component.handleDummyRemoval(dummy.get().id(), new DummyDeathContext(deathReason, applyRewards, null, null, null));
    }

    public static @Nullable UUID findClosestDummyOnSegment(PlayerEntity viewer, Vec3d start, Vec3d end) {
        if (viewer == null || start == null || end == null) {
            return null;
        }

        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(viewer);
        return component.getDummyStates().stream()
                .filter(state -> state.kind() == HallucinationDummyKind.KILLER)
                .map(state -> {
                    Optional<Vec3d> hit = getDummyBox(state).raycast(start, end);
                    if (hit.isEmpty()) {
                        return null;
                    }
                    return new DummyHit(state.id(), start.squaredDistanceTo(hit.get()));
                })
                .filter(hit -> hit != null)
                .min(Comparator.comparingDouble(DummyHit::distanceSq))
                .map(DummyHit::dummyId)
                .orElse(null);
    }

    public static boolean tryKillDummyOnSegment(PlayerEntity viewer,
                                                Vec3d start,
                                                Vec3d end,
                                                Identifier deathReason,
                                                boolean applyRewards,
                                                @Nullable UUID bombOwnerUuid,
                                                @Nullable UUID poisonerUuid,
                                                @Nullable Identifier poisonSource) {
        UUID dummyId = findClosestDummyOnSegment(viewer, start, end);
        if (dummyId == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(viewer).handleDummyRemoval(
                dummyId,
                new DummyDeathContext(deathReason, applyRewards, bombOwnerUuid, poisonerUuid, poisonSource)
        );
    }

    public static int killDummiesInBox(PlayerEntity viewer,
                                       Box box,
                                       Identifier deathReason,
                                       boolean applyRewards,
                                       @Nullable UUID bombOwnerUuid,
                                       @Nullable UUID poisonerUuid,
                                       @Nullable Identifier poisonSource) {
        if (viewer == null || box == null) {
            return 0;
        }

        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(viewer);
        List<UUID> victims = component.getDummyStates().stream()
                .filter(state -> state.kind() == HallucinationDummyKind.KILLER)
                .filter(state -> getDummyBox(state).intersects(box))
                .map(HallucinationDummyState::id)
                .toList();

        int killed = 0;
        for (UUID dummyId : victims) {
            if (component.handleDummyRemoval(dummyId, new DummyDeathContext(deathReason, applyRewards, bombOwnerUuid, poisonerUuid, poisonSource))) {
                killed++;
            }
        }
        return killed;
    }

    public static boolean isPointInsideDummy(PlayerEntity viewer, UUID dummyId, Vec3d point) {
        if (viewer == null || dummyId == null || point == null) {
            return false;
        }
        return HallucinationPlayerComponent.KEY.get(viewer)
                .getDummy(dummyId)
                .filter(state -> state.kind() == HallucinationDummyKind.KILLER)
                .map(HallucinationDummyInteractionHelper::getDummyBox)
                .map(box -> box.contains(point))
                .orElse(false);
    }

    public static Box getDummyBox(HallucinationDummyState state) {
        return getDummyBox(state.position());
    }

    public static Box getDummyBox(Vec3d pos) {
        return new Box(
                pos.x - DUMMY_HALF_WIDTH,
                pos.y,
                pos.z - DUMMY_HALF_WIDTH,
                pos.x + DUMMY_HALF_WIDTH,
                pos.y + DUMMY_HEIGHT,
                pos.z + DUMMY_HALF_WIDTH
        );
    }

    public static ActionResult failIfInvalidViewer(PlayerEntity user) {
        if (user == null || !GameFunctions.isPlayerAliveAndSurvival(user)) {
            return ActionResult.PASS;
        }
        return null;
    }

    public static boolean isServerPlayer(PlayerEntity user) {
        return user instanceof ServerPlayerEntity;
    }

    public static boolean isServerSide(World world) {
        return world != null && !world.isClient;
    }

    private record DummyHit(UUID dummyId, double distanceSq) {
    }
}
