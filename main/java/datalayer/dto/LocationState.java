package datalayer.dto;

import core.domain.space.Point;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class LocationState {
    private List<RoomState> rooms;
    private List<TunnelState> tunnels;
    private Point exit;
}