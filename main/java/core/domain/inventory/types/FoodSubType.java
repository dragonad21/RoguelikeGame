package core.domain.inventory.types;

public enum FoodSubType implements SubType {
    MEAT("Meat"),
    BREAD("Bread"),
    MILK("Milk");

    private final String name;

    FoodSubType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}