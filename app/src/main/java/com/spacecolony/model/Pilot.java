package com.spacecolony.model;

import com.spacecolony.R;

/**
 * Pilot specialization. Balanced stats; gets bonus skill on navigation-type missions.
 * Default: skill=5, resilience=4, maxEnergy=20. Color: Blue.
 */
public class Pilot extends CrewMember {

    private static final int BASE_SKILL = 5;
    private static final int BASE_RESILIENCE = 4;
    private static final int BASE_MAX_ENERGY = 20;

    public Pilot(String name) {
        super(name, "Pilot", BASE_SKILL, BASE_RESILIENCE, BASE_MAX_ENERGY);
    }

    /**
     * Pilots get a +2 bonus on navigation-type missions (Specialization Bonus feature).
     * Base act uses effective skill.
     */
    @Override
    public int act() {
        return getEffectiveSkill();
    }

    /**
     * Pilot gets +2 skill bonus when mission type is NAVIGATION.
     */
    public int actWithBonus(MissionType missionType) {
        int bonus = (missionType == MissionType.NAVIGATION) ? 2 : 0;
        return getEffectiveSkill() + bonus;
    }

    @Override
    public int getColorResId() {
        return R.color.pilot_blue;
    }

    @Override
    public int getAvatarResId() {
        return R.drawable.ic_pilot;
    }

    @Override
    public String getSpecialAbilityDescription() {
        return "Navigation Expert: +2 skill on navigation missions";
    }
}
