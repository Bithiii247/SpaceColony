package com.spacecolony.model;

import com.spacecolony.R;

/**
 * Medic specialization. High skill; can heal a partner once per mission.
 * Default: skill=7, resilience=2, maxEnergy=18. Color: Green.
 */
public class Medic extends CrewMember {

    private static final int BASE_SKILL = 7;
    private static final int BASE_RESILIENCE = 2;
    private static final int BASE_MAX_ENERGY = 18;

    private boolean healUsed = false;

    public Medic(String name) {
        super(name, "Medic", BASE_SKILL, BASE_RESILIENCE, BASE_MAX_ENERGY);
    }

    @Override
    public int act() {
        return getEffectiveSkill();
    }

    /**
     * Medic heals a partner by restoring energy. Can only be used once per mission.
     * Returns amount healed, or 0 if already used.
     */
    public int healPartner(CrewMember partner) {
        if (!healUsed && partner != null && partner.isAlive()) {
            int healAmount = getEffectiveSkill();
            int newEnergy = Math.min(partner.getMaxEnergy(), partner.getEnergy() + healAmount);
            partner.setEnergy(newEnergy);
            healUsed = true;
            return newEnergy - partner.getEnergy() + healAmount; // actual healed
        }
        return 0;
    }

    public boolean isHealUsed() { return healUsed; }

    public void resetHeal() { healUsed = false; }

    @Override
    public int getColorResId() {
        return R.color.medic_green;
    }

    @Override
    public int getAvatarResId() {
        return R.drawable.ic_medic;
    }

    @Override
    public String getSpecialAbilityDescription() {
        return "Field Medic: Can heal partner once per mission";
    }
}
