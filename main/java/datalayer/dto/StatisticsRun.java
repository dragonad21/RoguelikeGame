package datalayer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class StatisticsRun {
    @Setter
    private int treasures;
    private int level;
    private int kills;
    private int foodApply;
    private int elixirsApply;
    private int scrollsApply;
    private int hits;
    private int misses;
    private int steps;

    public StatisticsRun() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public StatisticsRun(int treasures, int level, int kills, int foodApply,
                         int elixirsApply, int scrollsApply, int hits, int misses, int steps) {
        this.treasures = treasures;
        this.level = level;
        this.kills = kills;
        this.foodApply = foodApply;
        this.elixirsApply = elixirsApply;
        this.scrollsApply = scrollsApply;
        this.hits = hits;
        this.misses = misses;
        this.steps = steps;
    }

    public void upLevel() {
        level++;
    }

    public void upKills() {
        kills++;
    }

    public void upFood() {
        foodApply++;
    }

    public void upElixirs() {
        elixirsApply++;
    }

    public void upScrolls() {
        scrollsApply++;
    }

    public void upHits() {
        hits++;
    }

    public void upMisses() {
        misses++;
    }

    public void upSteps() {
        steps++;
    }

}
