package com.spacecolony.model;

import java.io.Serializable;

/**
 * Abstract base class representing a crew member in the Space Colony.
 * Implements inheritance and polymorphism as required by OOP principles.
 */
public abstract class CrewMember implements Serializable {

    // ── Fields ──────────────────────────────────────────────────────────────
    private static int idCounter = 0;

    private final int id;
    private String name;
    private final String specialization;
    private int skill;
    private final int resilience;
    private int experience;
    private int energy;
    private final int maxEnergy;
    private Location location;

    // Statistics (Bonus: Statistics feature)
    private int missionsCompleted;
    private int missionsWon;
    private int trainingSessions;

    // ── Constructor ──────────────────────────────────────────────────────────
    public CrewMember(String name, String specialization, int skill, int resilience, int maxEnergy) {
        this.id = ++idCounter;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
        this.location = Location.QUARTERS;
        this.missionsCompleted = 0;
        this.missionsWon = 0;
        this.trainingSessions = 0;
    }

    // ── Abstract Methods (Polymorphism) ───────────────────────────────────────
    /**
     * Returns the effective attack damage dealt to a target.
     * Subclasses can override for specialization bonuses.
     */
    public abstract int act();

    /**
     * Returns the color resource ID representing this crew member's specialization.
     */
    public abstract int getColorResId();

    /**
     * Returns the drawable resource ID representing this crew member's avatar.
     */
    public abstract int getAvatarResId();

    /**
     * Returns a specialization-specific bonus description.
     */
    public abstract String getSpecialAbilityDescription();

    // ── Core Game Logic ───────────────────────────────────────────────────────
    /**
     * Defends against incoming damage. Returns actual damage taken.
     * damage = attacker's skill - this.resilience (min 1)
     */
    public int defend(int incomingDamage) {
        int actualDamage = Math.max(1, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    /**
     * Awards experience and updates skill.
     */
    public void gainExperience(int points) {
        experience += points;
        // Skill increases with experience (effective skill = base + experience)
    }

    /**
     * Returns effective skill including experience bonus.
     */
    public int getEffectiveSkill() {
        return skill + experience;
    }

    /**
     * Fully restores energy when crew member returns to Quarters.
     */
    public void restoreEnergy() {
        this.energy = this.maxEnergy;
    }

    /**
     * Returns true if crew member is still alive (energy > 0).
     */
    public boolean isAlive() {
        return energy > 0;
    }

    // ── Statistics ────────────────────────────────────────────────────────────
    public void recordMissionCompleted(boolean won) {
        missionsCompleted++;
        if (won) missionsWon++;
    }

    public void recordTrainingSession() {
        trainingSessions++;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getExperience() { return experience; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(maxEnergy, energy)); }
    public int getMaxEnergy() { return maxEnergy; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getMissionsWon() { return missionsWon; }
    public int getTrainingSessions() { return trainingSessions; }

    public static void resetIdCounter() { idCounter = 0; }
    public static int getIdCounter() { return idCounter; }
    public static void setIdCounter(int val) { idCounter = val; }

    @Override
    public String toString() {
        return specialization + "(" + name + ") skill:" + getEffectiveSkill()
                + " res:" + resilience + " exp:" + experience
                + " energy:" + energy + "/" + maxEnergy;
    }
}
