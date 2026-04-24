package org.agmas.noellesroles.hallucination;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record DummyDeathContext(
        Identifier deathReason,
        boolean applyRewards,
        @Nullable UUID bombOwnerUuid,
        @Nullable UUID poisonerUuid,
        @Nullable Identifier poisonSource
) {
}
