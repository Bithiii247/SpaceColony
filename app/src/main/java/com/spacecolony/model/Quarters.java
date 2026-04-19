package com.spacecolony.model;

import java.util.List;

/**
 * Quarters is the home location for crew members.
 * Handles crew member creation, movement, and energy restoration.
 */
public class Quarters {

    private final Storage storage;

    public Quarters() {
        this.storage = Storage.getInstance();
    }

    /**
     * Creates a new crew member and places them in Quarters.
     */
    public CrewMember createCrewMember(String name, String specialization) {
        CrewMember cm;
        switch (specialization) {
            case "Pilot":
                cm = new Pilot(name);
                break;
            case "Engineer":
                cm = new Engineer(name);
                break;
            case "Medic":
                cm = new Medic(name);
                break;
            case "Scientist":
                cm = new Scientist(name);
                break;
            case "Soldier":
                cm = new Soldier(name);
                break;
            default:
                throw new IllegalArgumentException("Unknown specialization: " + specialization);
        }
        cm.setLocation(Location.QUARTERS);
        storage.addCrewMember(cm);
        return cm;
    }

    /**
     * Moves a crew member to the Simulator.
     */
    public void moveToSimulator(CrewMember cm) {
        cm.setLocation(Location.SIMULATOR);
    }

    /**
     * Moves a crew member to Mission Control.
     */
    public void moveToMissionControl(CrewMember cm) {
        cm.setLocation(Location.MISSION_CONTROL);
    }

    /**
     * Restores energy for a crew member returning to Quarters.
     * Experience is retained.
     */
    public void restoreEnergy(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(Location.QUARTERS);
    }

    /**
     * Returns all crew members currently in Quarters.
     */
    public List<CrewMember> getResidents() {
        return storage.getCrewByLocation(Location.QUARTERS);
    }
}
