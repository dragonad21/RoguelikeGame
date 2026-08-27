package datalayer.dto;

import core.domain.space.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomState {
    private int width;
    private int height;
    private Point position;
    private boolean start;
    private Point centre;
    private Point randomPointInRoom;
}