package com.spacecolony.model;

import com.spacecolony.R;

/**
 * Soldier specialization. Highest skill, zero resilience — glass cannon.
 * Gets bonus on combat missions.
 * Default: skill=9, resilience=0, maxEnergy=16. Color: Red.
 */
public class Soldier extends CrewMember {

    private static final int BASE_SKILL = 9;
    private static final int BASE_RESILIENCE = 0;
    private static final int BASE_MAX_ENERGY = 16;

    public Soldier(String name) {
        super(name, "Soldier", BASE_SKILL, BASE_RESILIENCE, BASE_MAX_ENERGY);
    }

    @Override
    public int act() {
        return getEffectiveSkill();
    }

    /**
     * Soldier gets +2 skill on COMBAT missions (Specialization Bonus feature).
     */
    public int actWithBonus(MissionType missionType) {
        int bonus = (missionType == MissionType.COMBAT) ? 2 : 0;
        return getEffectiveSkill() + bonus;
    }

    @Override
    public int getColorResId() {
        return R.color.soldier_red;
    }

    @Override
    public int getAvatarResId() {
        return R.drawable.ic_soldier;
    }

    @Override
    public String getSpecialAbilityDescription() {
        return "Combat Specialist: +2 skill on combat missions";
    }
}
