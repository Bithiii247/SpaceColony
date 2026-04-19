package com.spacecolony.model;

import com.spacecolony.R;

/**
 * Scientist specialization. Highest base skill, lowest resilience.
 * Gets bonus on research/alien missions.
 * Default: skill=8, resilience=1, maxEnergy=17. Color: Purple.
 */
public class Scientist extends CrewMember {

    private static final int BASE_SKILL = 8;
    private static final int BASE_RESILIENCE = 1;
    private static final int BASE_MAX_ENERGY = 17;

    public Scientist(String name) {
        super(name, "Scientist", BASE_SKILL, BASE_RESILIENCE, BASE_MAX_ENERGY);
    }

    @Override
    public int act() {
        return getEffectiveSkill();
    }

    /**
     * Scientist gets +2 skill on ALIEN missions (Specialization Bonus feature).
     */
    public int actWithBonus(MissionType missionType) {
        int bonus = (missionType == MissionType.ALIEN) ? 2 : 0;
        return getEffectiveSkill() + bonus;
    }

    @Override
    public int getColorResId() {
        return R.color.scientist_purple;
    }

    @Override
    public int getAvatarResId() {
        return R.drawable.ic_scientist;
    }

    @Override
    public String getSpecialAbilityDescription() {
        return "Xenologist: +2 skill on alien contact missions";
    }
}
