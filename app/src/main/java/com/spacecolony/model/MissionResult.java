package com.spacecolony.model;

import java.util.List;

/**
 * Holds the result of a completed mission, including the log of events.
 */
public class MissionResult {

    private final boolean victory;
    private final List<String> log;
    private final List<Integer> survivorIds;

    public MissionResult(boolean victory, List<String> log, List<Integer> survivorIds) {
        this.victory = victory;
        this.log = log;
        this.survivorIds = survivorIds;
    }

    public boolean isVictory() { return victory; }
    public List<String> getLog() { return log; }
    public List<Integer> getSurvivorIds() { return survivorIds; }
}
