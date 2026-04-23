package org.agmas.noellesroles.hallucination;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record KillRewardContext(
        @Nullable PlayerEntity victim,
        @Nullable PlayerEntity killer,
        boolean hallucinationDummy,
        Identifier deathReason,
        @Nullable UUID bombOwnerUuid,
        @Nullable UUID poisonerUuid,
        @Nullable Identifier poisonSource
) {
}
