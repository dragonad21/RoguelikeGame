package datalayer.dto;

import core.domain.space.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemState {
    private String type;
    private String subType;
    private int value;
    private Point position;
}