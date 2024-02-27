package com.runicrealms.plugin.professions.utilities;

/**
 * Used to convert gathering experience to combat experience
 */
public class ExperienceConverter {

    /**
     * Uses a natural logarithmic function (log1p is ln(x+1))
     *
     * @param gatheringExperience the amount of gathering experience
     * @param multiplier          an additional lever to affect the combat exp
     * @return the combat experience to award
     */
    public static int calculateCombatExperience(int gatheringExperience, double multiplier, int scalar) {
        // Use Math.log1p for computing log(x+1) to get accurate results for small x
        // We multiply the result by MULTIPLIER to get the actual experience
        double combatExperience = multiplier * Math.log1p(gatheringExperience);

        // Convert the result to int because experience is usually an integer
        // Use Math.round to round the nearest integer instead of truncating
        return scalar * (int) Math.round(combatExperience);
    }

}
