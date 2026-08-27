package core.domain.space.path;

import core.domain.space.Direction;
import core.domain.space.Point;

public record Node(Point point, Direction direction) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return point.equals(node.point);
    }
}
