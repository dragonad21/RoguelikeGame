package core.domain.inventory;

import core.domain.space.Point;

public record LocationItem(Point position, Item<?> item) {
}