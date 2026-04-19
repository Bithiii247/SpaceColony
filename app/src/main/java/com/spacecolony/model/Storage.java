package com.spacecolony.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton storage class for crew members.
 * Uses HashMap<Integer, CrewMember> to store crew members by their unique ID.
 */
public class Storage {

    private static Storage instance;

    private final HashMap<Integer, CrewMember> crewMembers;
    private String colonyName;

    // Colony-wide statistics (Bonus: Statistics feature)
    private int totalMissions;
    private int totalRecruits;

    private Storage() {
        crewMembers = new HashMap<>();
        colonyName = "Horizon Station";
        totalMissions = 0;
        totalRecruits = 0;
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    // ── Crew Member Management ────────────────────────────────────────────────

    /**
     * Adds a crew member to storage.
     */
    public void addCrewMember(CrewMember cm) {
        crewMembers.put(cm.getId(), cm);
        totalRecruits++;
    }

    /**
     * Retrieves a crew member by ID.
     */
    public CrewMember getCrewMember(int id) {
        return crewMembers.get(id);
    }

    /**
     * Removes a crew member by ID (on death).
     */
    public void removeCrewMember(int id) {
        crewMembers.remove(id);
    }

    /**
     * Returns all crew members in a specific location.
     */
    public List<CrewMember> getCrewByLocation(Location location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMembers.values()) {
            if (cm.getLocation() == location) {
                result.add(cm);
            }
        }
        return result;
    }

    /**
     * Returns all crew members.
     */
    public List<CrewMember> getAllCrew() {
        return new ArrayList<>(crewMembers.values());
    }

    /**
     * Returns total count of crew members.
     */
    public int getCrewCount() {
        return crewMembers.size();
    }

    /**
     * Returns count of crew members by location.
     */
    public int getCountByLocation(Location location) {
        int count = 0;
        for (CrewMember cm : crewMembers.values()) {
            if (cm.getLocation() == location) count++;
        }
        return count;
    }

    // ── Statistics ────────────────────────────────────────────────────────────
    public void incrementMissions() { totalMissions++; }
    public int getTotalMissions() { return totalMissions; }
    public int getTotalRecruits() { return totalRecruits; }

    // ── Colony ────────────────────────────────────────────────────────────────
    public String getColonyName() { return colonyName; }
    public void setColonyName(String name) { this.colonyName = name; }

    public HashMap<Integer, CrewMember> getCrewMap() { return crewMembers; }

    /** Resets all data (for testing). */
    public void reset() {
        crewMembers.clear();
        totalMissions = 0;
        totalRecruits = 0;
        CrewMember.resetIdCounter();
    }
}
