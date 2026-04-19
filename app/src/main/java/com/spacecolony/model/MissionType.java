package com.spacecolony.model;

/**
 * Enum representing different mission types.
 * Used for specialization bonuses (Bonus Feature).
 */
public enum MissionType {
    NAVIGATION("Asteroid Field Navigation"),
    REPAIR("Station Repair"),
    ALIEN("Alien Contact"),
    COMBAT("Pirate Assault"),
    HAZARD("Solar Flare Emergency");

    private final String displayName;

    MissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
