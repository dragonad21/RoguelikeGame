package datalayer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class LevelState {
    private LocationState location;
    private List<EnemyState> enemies;
    private List<ItemState> items;
    private int levelNumber;
}