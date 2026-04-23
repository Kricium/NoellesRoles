package org.agmas.noellesroles.hallucination;

import java.util.UUID;

public record HallucinationDummyBombState(
        int bombTimer,
        int beepTimer,
        boolean beeping,
        UUID bomberUuid
) {
    public HallucinationDummyBombState withBombTimer(int nextBombTimer) {
        return new HallucinationDummyBombState(nextBombTimer, this.beepTimer, this.beeping, this.bomberUuid);
    }

    public HallucinationDummyBombState withBeepTimer(int nextBeepTimer) {
        return new HallucinationDummyBombState(this.bombTimer, nextBeepTimer, this.beeping, this.bomberUuid);
    }

    public HallucinationDummyBombState startBeeping(int nextBeepTimer) {
        return new HallucinationDummyBombState(0, nextBeepTimer, true, this.bomberUuid);
    }
}
