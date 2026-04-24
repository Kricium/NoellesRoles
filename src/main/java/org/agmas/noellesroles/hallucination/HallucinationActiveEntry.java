package org.agmas.noellesroles.hallucination;

import java.util.UUID;

public record HallucinationActiveEntry(
        HallucinationEffectId effectId,
        int remainingTicks,
        int stackValue,
        HallucinationTargetKind targetKind,
        UUID targetUuid,
        HallucinationUiSlot uiSlot
) {
    public HallucinationActiveEntry withRemainingTicks(int remainingTicks) {
        return new HallucinationActiveEntry(effectId, remainingTicks, stackValue, targetKind, targetUuid, uiSlot);
    }

    public HallucinationActiveEntry withStackValue(int stackValue) {
        return new HallucinationActiveEntry(effectId, remainingTicks, stackValue, targetKind, targetUuid, uiSlot);
    }
}
