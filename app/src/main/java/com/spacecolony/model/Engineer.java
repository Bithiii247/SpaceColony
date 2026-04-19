package com.spacecolony.model;

import com.spacecolony.R;

/**
 * Engineer specialization. Higher skill than Pilot; bonus on repair missions.
 * Default: skill=6, resilience=3, maxEnergy=19. Color: Yellow.
 */
public class Engineer extends CrewMember {

    private static final int BASE_SKILL = 6;
    private static final int BASE_RESILIENCE = 3;
    private static final int BASE_MAX_ENERGY = 19;

    public Engineer(String name) {
        super(name, "Engineer", BASE_SKILL, BASE_RESILIENCE, BASE_MAX_ENERGY);
    }

    @Override
    public int act() {
        return getEffectiveSkill();
    }

    /**
     * Engineer gets +2 skill on REPAIR missions (Specialization Bonus feature).
     */
    public int actWithBonus(MissionType missionType) {
        int bonus = (missionType == MissionType.REPAIR) ? 2 : 0;
        return getEffectiveSkill() + bonus;
    }

    @Override
    public int getColorResId() {
        return R.color.engineer_yellow;
    }

    @Override
    public int getAvatarResId() {
        return R.drawable.ic_engineer;
    }

    @Override
    public String getSpecialAbilityDescription() {
        return "Repair Specialist: +2 skill on repair missions";
    }
}
