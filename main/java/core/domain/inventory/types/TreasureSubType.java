package core.domain.inventory.types;

public enum TreasureSubType implements SubType {
    GEM("Gem"),
    GOLD("Gold"),
    DIAMOND("Diamond"),
    JEWEL("Jewel");

    private final String name;

    TreasureSubType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
