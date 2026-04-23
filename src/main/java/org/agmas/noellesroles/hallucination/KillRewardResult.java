package org.agmas.noellesroles.hallucination;

public record KillRewardResult(int moneyDelta, int timeDeltaSeconds) {
    public static final KillRewardResult NONE = new KillRewardResult(0, 0);

    public KillRewardResult add(KillRewardResult other) {
        if (other == null) {
            return this;
        }
        return new KillRewardResult(this.moneyDelta + other.moneyDelta, this.timeDeltaSeconds + other.timeDeltaSeconds);
    }
}
