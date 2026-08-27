package datalayer.dto;

import core.domain.space.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EnemyState {
    private String type;
    private int health;
    private int maxHealth;
    private int strength;
    private int dexterity;
    private Point position;
    private int levelNumber;
}