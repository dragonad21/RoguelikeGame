package core.domain.inventory.types;

import lombok.Getter;

public enum ScrollSubType implements SubType {
    STRENGTH("Strength", StatType.STRENGTH),
    DEXTERITY("Dexterity", StatType.DEXTERITY),
    MAX_HEALTH("Max Health", StatType.MAX_HEALTH);

    private final String name;
    @Getter
    private final StatType stat;

    ScrollSubType(String name, StatType stat) {
        this.name = name;
        this.stat = stat;
    }

    @Override
    public String getName() {
        return name;
    }

}
