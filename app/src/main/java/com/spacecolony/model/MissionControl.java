package com.spacecolony.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MissionControl manages the mission system.
 * Creates threats and executes the cooperative turn-based mission algorithm.
 */
public class MissionControl {

    private static int missionCount = 0;  // static counter for difficulty scaling
    private final Storage storage;
    private final Random random;

    public MissionControl() {
        this.storage = Storage.getInstance();
        this.random = new Random();
    }

    /**
     * Generates a threat scaled to current mission count.
     */
    public Threat generateThreat(MissionType missionType) {
        return new Threat(missionType, missionCount);
    }

    /**
     * Executes the cooperative mission algorithm as specified in the project.
     * Returns a MissionResult with the full event log.
     *
     * @param memberA   First crew member
     * @param memberB   Second crew member
     * @param threat    The generated threat
     * @param actions   List of actions per turn: "ATTACK" or "DEFEND" (Tactical Combat bonus)
     * @return          MissionResult with victory status and log
     */
    public MissionResult launchMission(CrewMember memberA, CrewMember memberB,
                                       Threat threat, List<String> actions) {
        List<String> log = new ArrayList<>();
        List<Integer> survivorIds = new ArrayList<>();

        // Reset medic heal if applicable
        if (memberA instanceof Medic) ((Medic) memberA).resetHeal();
        if (memberB instanceof Medic) ((Medic) memberB).resetHeal();

        MissionType missionType = threat.getMissionType();

        log.add("=== MISSION: " + missionType.getDisplayName() + " ===");
        log.add("Threat: " + threat.toString());
        log.add(memberA.toString());
        log.add(memberB.toString());

        int actionIndex = 0;
        int round = 1;

        // Alias active members (null if dead)
        CrewMember[] crew = {memberA, memberB};
        boolean[] alive = {true, true};

        // Mission loop
        while (!threat.isDefeated() && (alive[0] || alive[1])) {
            log.add("\n--- Round " + round + " ---");

            for (int i = 0; i < 2; i++) {
                if (!alive[i]) continue;
                if (threat.isDefeated()) break;

                CrewMember current = crew[i];

                // Determine action: from list if available, else default ATTACK
                String action = "ATTACK";
                if (actions != null && actionIndex < actions.size()) {
                    action = actions.get(actionIndex++);
                }

                int damageDealt;
                if ("DEFEND".equals(action)) {
                    // Defend: crew member braces — skip attack, halve next threat damage
                    log.add(current.getSpecialization() + "(" + current.getName() + ") DEFENDS");
                    // Give temporary resilience boost (applied via flag in next threat attack)
                    // Simplified: crew member gains +3 effective resilience this turn
                    log.add("  " + current.getName() + " braces for impact (+3 resilience this turn)");
                    // Threat attacks with reduced damage
                    int raw = threat.getSkill();
                    int reduced = Math.max(1, raw - current.getResilience() - 3);
                    current.setEnergy(Math.max(0, current.getEnergy() - reduced));
                    log.add("  Threat retaliates: damage dealt = " + raw + " - "
                            + (current.getResilience() + 3) + " = " + reduced);
                    log.add("  " + current.getName() + " energy: " + current.getEnergy()
                            + "/" + current.getMaxEnergy());
                } else if ("HEAL".equals(action) && current instanceof Medic) {
                    // Medic heals partner
                    int partnerIdx = (i == 0) ? 1 : 0;
                    CrewMember partner = alive[partnerIdx] ? crew[partnerIdx] : null;
                    Medic medic = (Medic) current;
                    if (!medic.isHealUsed() && partner != null) {
                        int healed = current.getEffectiveSkill();
                        int before = partner.getEnergy();
                        partner.setEnergy(Math.min(partner.getMaxEnergy(), partner.getEnergy() + healed));
                        int actual = partner.getEnergy() - before;
                        medic.resetHeal(); // mark used
                        log.add(current.getSpecialization() + "(" + current.getName()
                                + ") HEALS " + partner.getName() + " for " + actual + " energy");
                        log.add("  " + partner.getName() + " energy: " + partner.getEnergy()
                                + "/" + partner.getMaxEnergy());
                    } else {
                        // Fall back to attack if heal unavailable
                        damageDealt = actWithSpecBonus(current, missionType, threat, log);
                    }
                    // Threat still retaliates
                    int threatDmg = threat.attack(current);
                    log.add("  Threat retaliates against " + current.getName()
                            + ": damage = " + threatDmg);
                    log.add("  " + current.getName() + " energy: " + current.getEnergy()
                            + "/" + current.getMaxEnergy());
                } else {
                    // Default: ATTACK
                    damageDealt = actWithSpecBonus(current, missionType, threat, log);

                    // Threat retaliates (if still alive)
                    if (!threat.isDefeated()) {
                        int threatDmg = threat.attack(current);
                        log.add("  Threat retaliates against " + current.getName()
                                + ": damage = " + threatDmg);
                        log.add("  " + current.getName() + " energy: " + current.getEnergy()
                                + "/" + current.getMaxEnergy());
                    }
                }

                // Check if crew member died
                if (!current.isAlive()) {
                    alive[i] = false;
                    log.add("  ⚠ " + current.getName() + " has been incapacitated!");
                }
            }

            round++;
        }

        // Resolve outcome
        boolean victory = threat.isDefeated();
        missionCount++;
        storage.incrementMissions();

        if (victory) {
            log.add("\n=== MISSION COMPLETE ===");
            log.add("The " + threat.getName() + " has been neutralized!");
            for (int i = 0; i < 2; i++) {
                if (alive[i]) {
                    crew[i].gainExperience(1);
                    crew[i].recordMissionCompleted(true);
                    crew[i].setLocation(Location.MISSION_CONTROL);
                    survivorIds.add(crew[i].getId());
                    log.add(crew[i].getName() + " gains 1 experience point. (exp: "
                            + crew[i].getExperience() + ")");
                }
            }
        } else {
            log.add("\n=== MISSION FAILED ===");
            log.add("Mission failed. All crew members lost.");
        }

        // Handle defeated crew (Bonus: No Death – send to Medbay)
        for (int i = 0; i < 2; i++) {
            crew[i].recordMissionCompleted(victory && alive[i]);
            if (!alive[i]) {
                // Send to Medbay instead of permanent death
                crew[i].setLocation(Location.MEDBAY);
                log.add(crew[i].getName() + " has been sent to Medbay for recovery.");
            }
        }

        return new MissionResult(victory, log, survivorIds);
    }

    /**
     * Helper: executes an attack with specialization bonus and logs it.
     */
    private int actWithSpecBonus(CrewMember cm, MissionType missionType, Threat threat, List<String> log) {
        int rawDamage;
        if (cm instanceof Pilot) {
            rawDamage = ((Pilot) cm).actWithBonus(missionType);
        } else if (cm instanceof Engineer) {
            rawDamage = ((Engineer) cm).actWithBonus(missionType);
        } else if (cm instanceof Scientist) {
            rawDamage = ((Scientist) cm).actWithBonus(missionType);
        } else if (cm instanceof Soldier) {
            rawDamage = ((Soldier) cm).actWithBonus(missionType);
        } else {
            rawDamage = cm.act();
        }

        int damageTaken = threat.defend(rawDamage);
        log.add(cm.getSpecialization() + "(" + cm.getName() + ") attacks "
                + threat.getName());
        log.add("  Damage dealt: " + rawDamage + " - " + threat.getResilience()
                + " = " + damageTaken);
        log.add("  " + threat.getName() + " energy: " + threat.getEnergy()
                + "/" + threat.getMaxEnergy());
        return damageTaken;
    }

    /**
     * Moves a crew member to Quarters and restores their energy.
     */
    public void returnToQuarters(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(Location.QUARTERS);
    }

    /**
     * Returns a random mission type.
     */
    public MissionType randomMissionType() {
        MissionType[] types = MissionType.values();
        return types[random.nextInt(types.length)];
    }

    /**
     * Returns all crew members in Mission Control.
     */
    public List<CrewMember> getReadyCrew() {
        return storage.getCrewByLocation(Location.MISSION_CONTROL);
    }

    public static int getMissionCount() { return missionCount; }
    public static void resetMissionCount() { missionCount = 0; }
}
