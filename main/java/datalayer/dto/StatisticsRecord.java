package datalayer.dto;

import datalayer.dto.StatisticsRun;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatisticsRecord {
    private int treasures;
    private int level;
    private int kills;
    private int foodApply;
    private int elixirsApply;
    private int scrollsApply;
    private int hits;
    private int misses;
    private int steps;

    public StatisticsRecord(StatisticsRun run) {
        this.treasures = run.getTreasures();
        this.level = run.getLevel();
        this.kills = run.getKills();
        this.foodApply = run.getFoodApply();
        this.elixirsApply = run.getElixirsApply();
        this.scrollsApply = run.getScrollsApply();
        this.hits = run.getHits();
        this.misses = run.getMisses();
        this.steps = run.getSteps();
    }

}
