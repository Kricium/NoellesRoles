package org.agmas.noellesroles.hallucination;

import net.minecraft.util.Identifier;

import java.util.UUID;

public record HallucinationDummyPoisonState(
        int poisonTicks,
        UUID poisonerUuid,
        Identifier poisonSource
) {
    public HallucinationDummyPoisonState withPoisonTicks(int nextPoisonTicks) {
        return new HallucinationDummyPoisonState(nextPoisonTicks, this.poisonerUuid, this.poisonSource);
    }
}
