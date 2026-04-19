package com.spacecolony.model;

import java.util.Random;

/**
 * Represents a system-generated threat that crew members fight against.
 * Difficulty scales with the number of completed missions.
 */
public class Threat {

    private final String name;
    private final MissionType missionType;
    private int skill;
    private final int resilience;
    private int energy;
    private final int maxEnergy;

    private static final Random random = new Random();

    // Threat names per mission type
    private static final String[][] THREAT_NAMES = {
        {"Asteroid Storm", "Meteor Shower", "Space Debris Field"},   // NAVIGATION
        {"Hull Breach", "Reactor Meltdown", "Power Failure"},        // REPAIR
        {"Alien Scouts", "Xenomorph Pack", "Hive Mind Signal"},      // ALIEN
        {"Pirate Raiders", "Rogue Drones", "Armed Mutineers"},       // COMBAT
        {"Solar Flare", "Radiation Burst", "Magnetic Storm"}         // HAZARD
    };

    /**
     * Creates a threat scaled to mission difficulty.
     * Formula: skill = 4 + missionCount, resilience = 2, energy = 20 + missionCount * 2
     */
    public Threat(MissionType missionType, int missionCount) {
        this.missionType = missionType;
        int typeIndex = missionType.ordinal();
        String[] names = THREAT_NAMES[typeIndex];
        this.name = names[random.nextInt(names.length)];

        // Scaling formula as specified in the project
        this.skill = 4 + missionCount;
        this.resilience = 2;
        this.maxEnergy = 20 + missionCount * 2;
        this.energy = this.maxEnergy;
    }

    /**
     * Threat attacks a crew member. Returns damage dealt.
     */
    public int attack(CrewMember target) {
        // Add slight randomness (Bonus: Randomness feature)
        int randomBonus = random.nextInt(3); // 0, 1, or 2
        int rawDamage = skill + randomBonus;
        return target.defend(rawDamage);
    }

    /**
     * Threat defends against crew member attack.
     * Returns damage taken by the threat.
     */
    public int defend(int incomingDamage) {
        int damageTaken = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    // Getters
    public String getName() { return name; }
    public MissionType getMissionType() { return missionType; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }

    @Override
    public String toString() {
        return name + " (skill:" + skill + ", res:" + resilience
                + ", energy:" + energy + "/" + maxEnergy + ")";
    }
}
