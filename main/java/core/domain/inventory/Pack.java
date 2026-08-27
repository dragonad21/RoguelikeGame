package core.domain.inventory;

import java.util.ArrayList;
import java.util.List;

public class Pack {
    private static final int MAX_SIZE = 9;
    private final List<Item<?>> items;

    public Pack() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item<?> item) {
        if (items.size() >= MAX_SIZE) {
            return false;
        }
        items.add(item);
        return true;
    }

    public void removeItem(Item<?> item) {
        items.remove(item);
    }

    public List<Item<?>> getAllItems() {
        return items;
    }

    public Item<?> getItem(int i) {
        if (i < 1 || i > 9) {
            throw new IllegalArgumentException("Not allowed item index");
        }

        int index = i - 1;
        if (index >= items.size()) {
            throw new IndexOutOfBoundsException("There is no such thing in the pack");
        }

        return items.get(index);
    }
}