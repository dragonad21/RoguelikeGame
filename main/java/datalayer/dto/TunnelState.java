package datalayer.dto;

import core.domain.space.Door;
import core.domain.space.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TunnelState {
    private Point startRoomPosition;
    private Point endRoomPosition;
    private Point startPosition;
    private Point endPosition;
    private Door startDoor;
    private Door endDoor;
    private List<Point> points;
}