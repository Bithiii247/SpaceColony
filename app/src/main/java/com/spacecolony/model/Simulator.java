package com.spacecolony.model;

import java.util.List;

/**
 * Simulator handles training sessions for crew members.
 * Each training session grants 1 experience point.
 */
public class Simulator {

    private static final int XP_PER_SESSION = 1;
    private final Storage storage;

    public Simulator() {
        this.storage = Storage.getInstance();
    }

    /**
     * Trains a crew member, awarding experience points.
     * Experience increases effective skill (effectiveSkill = baseSkill + experience).
     */
    public void train(CrewMember cm) {
        cm.gainExperience(XP_PER_SESSION);
        cm.recordTrainingSession();
    }

    /**
     * Trains multiple selected crew members.
     */
    public void trainAll(List<CrewMember> selected) {
        for (CrewMember cm : selected) {
            train(cm);
        }
    }

    /**
     * Sends a crew member back to Quarters (restores energy).
     */
    public void sendToQuarters(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(Location.QUARTERS);
    }

    /**
     * Returns all crew members currently in the Simulator.
     */
    public List<CrewMember> getTrainees() {
        return storage.getCrewByLocation(Location.SIMULATOR);
    }
}
