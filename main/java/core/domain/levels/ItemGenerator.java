package core.domain.levels;

import core.domain.inventory.*;
import core.domain.inventory.types.*;
import core.util.Rng;

import java.util.ArrayList;
import java.util.List;

public class ItemGenerator {
    private final List<ItemTemplate> allItemTypes = new ArrayList<>();

    private record ItemTemplate(Item<?> item, int weight) {
    }

    public ItemGenerator() {
        registerItems();
    }

    public List<Item<?>> generateByLevel(int levelNumber) {
        int itemsCount = Rng.between((22 - levelNumber) / 7, (27 - levelNumber) / 3);

        List<Item<?>> items = new ArrayList<>();
        for (int i = 0; i < itemsCount; i++)
            items.add(generateItem());
        return items;
    }

    public List<Item<?>> generateByCount(int maxCount) {
        int itemsCount = maxCount > 0 ? Rng.between(0, maxCount) : 0;

        List<Item<?>> items = new ArrayList<>();
        for (int i = 0; i < itemsCount; i++)
            items.add(generateItem());
        return items;
    }

    private void registerItems() {
        allItemTypes.add(new ItemTemplate(new Food(FoodSubType.BREAD, Rng.between(2, 8)), Rng.between(1, 10)));
        allItemTypes.add(new ItemTemplate(new Food(FoodSubType.MEAT, Rng.between(4, 14)), Rng.between(1, 6)));
        allItemTypes.add(new ItemTemplate(new Food(FoodSubType.MILK, Rng.between(3, 12)), Rng.between(1, 8)));

        allItemTypes.add(new ItemTemplate(new Treasure(TreasureSubType.DIAMOND, Rng.between(15, 50)), 1));
        allItemTypes.add(new ItemTemplate(new Treasure(TreasureSubType.GEM, Rng.between(10, 35)), 1));
        allItemTypes.add(new ItemTemplate(new Treasure(TreasureSubType.GOLD, Rng.between(1, 15)), Rng.between(1, 12)));
        allItemTypes.add(new ItemTemplate(new Treasure(TreasureSubType.JEWEL, Rng.between(5, 20)), 1));

        allItemTypes.add(new ItemTemplate(new Weapon(WeaponSubType.DAGGER, Rng.between(1, 3)), 2));
        allItemTypes.add(new ItemTemplate(new Weapon(WeaponSubType.MACE, Rng.between(3, 7)), 2));
        allItemTypes.add(new ItemTemplate(new Weapon(WeaponSubType.LONGSWORD, Rng.between(5, 12)), 1));
        allItemTypes.add(new ItemTemplate(new Weapon(WeaponSubType.SPEAR, Rng.between(5, 9)), 1));

        allItemTypes.add(new ItemTemplate(new Elixir(ElixirSubType.MAX_HEALTH, Rng.between(4, 10)), Rng.between(1, 2)));
        allItemTypes.add(new ItemTemplate(new Elixir(ElixirSubType.DEXTERITY, Rng.between(1, 6)), Rng.between(1, 2)));
        allItemTypes.add(new ItemTemplate(new Elixir(ElixirSubType.STRENGTH, Rng.between(1, 6)), Rng.between(1, 2)));

        allItemTypes.add(new ItemTemplate(new Scroll(ScrollSubType.MAX_HEALTH, Rng.between(1, 4)), Rng.between(1, 2)));
        allItemTypes.add(new ItemTemplate(new Scroll(ScrollSubType.DEXTERITY, Rng.between(1, 4)), Rng.between(1, 2)));
        allItemTypes.add(new ItemTemplate(new Scroll(ScrollSubType.STRENGTH, Rng.between(1, 4)), Rng.between(1, 2)));
    }

    private Item<?> cloneItem(Item<?> originalItem) {
        if (originalItem instanceof Food t) {
            return new Food(t.getSubType(), t.getHealthRestore());
        } else if (originalItem instanceof Treasure t) {
            return new Treasure(t.getSubType(), t.getCost());
        } else if (originalItem instanceof Weapon t) {
            return new Weapon(t.getSubType(), t.getStrengthBonus());
        } else if (originalItem instanceof Elixir t) {
            return new Elixir(t.getSubType(), t.getBonusValue());
        } else if (originalItem instanceof Scroll t) {
            return new Scroll(t.getSubType(), t.getBonusValue());
        }
        return originalItem;
    }

    private Item<?> generateItem() {
        int sumWeight = allItemTypes.stream().mapToInt(t -> t.weight).sum();
        int randomValue = Rng.nextInt(sumWeight);

        int currentWeight = 0;
        for (ItemTemplate template : allItemTypes) {
            currentWeight += template.weight;
            if (randomValue < currentWeight)
                return cloneItem(template.item);
        }
        return allItemTypes.getFirst().item;
    }


}
