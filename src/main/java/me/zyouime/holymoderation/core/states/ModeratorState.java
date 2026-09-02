package me.zyouime.holymoderation.core.states;

import java.time.Instant;
import java.util.Optional;
import lombok.Getter;
import me.zyouime.holymoderation.core.dto.JournalProfile;
import me.zyouime.holymoderation.core.dto.JournalStats;

@Getter
public final class ModeratorState extends AbstractState {

    private JournalProfile profile;
    private JournalStats stats;
    private Instant profileUpdatedAt;
    private Instant statsUpdatedAt;

    public void profile(JournalProfile profile) {
        this.profile = profile;
        this.profileUpdatedAt = Instant.now();
    }

    public void stats(JournalStats stats) {
        this.stats = stats;
        this.statsUpdatedAt = Instant.now();
    }

    @Override
    public void reset() {
        this.profile = null;
        this.stats = null;
        this.profileUpdatedAt = null;
        this.statsUpdatedAt = null;
    }
}
