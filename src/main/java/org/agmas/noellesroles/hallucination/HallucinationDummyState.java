package org.agmas.noellesroles.hallucination;

import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record HallucinationDummyState(
        UUID id,
        HallucinationDummyKind kind,
        UUID skinUuid,
        String skinName,
        Vec3d position,
        boolean collidable,
        int localEntityId,
        float bodyYaw
) {
}
