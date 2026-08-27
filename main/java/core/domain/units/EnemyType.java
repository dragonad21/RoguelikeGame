package core.domain.units;

import core.domain.inventory.types.SubType;

public enum EnemyType implements SubType {
    ZOMBIE("Zombie"),
    VAMPIRE("Vampire"),
    GHOST("Ghost"),
    OGRE("Ogre"),
    SNAKE_MAGE("SnakeMage"),
    MIMIC("Mimic");

    private final String name;

    EnemyType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}