package core.domain.inventory.types;

public enum WeaponSubType implements SubType {
    MACE("Mace"),
    LONGSWORD("Long Sword"),
    SPEAR("Spear"),
    DAGGER("Dagger");

    private final String name;

    WeaponSubType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
