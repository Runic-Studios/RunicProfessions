package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.runicitems.item.RunicItem;

public class ProfUtil {

    public static final String REQUIRED_LEVEL_KEY = "requiredLevel";
    public static final String EXPERIENCE_KEY = "experience";

    /**
     * Helper function for RunicItemDynamic
     *
     * @param runicItem the runic item to read
     * @param key       the key of the data field
     * @return an Integer value matching key
     */
    public static double getRunicItemDataFieldDouble(RunicItem runicItem, String key) {
        try {
            return Double.parseDouble(runicItem.getData().get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    /**
     * Helper function for RunicItemDynamic
     *
     * @param runicItem the runic item to read
     * @param key       the key of the data field
     * @return an Integer value matching key
     */
    public static int getRunicItemDataFieldInt(RunicItem runicItem, String key) {
        try {
            return Integer.parseInt(runicItem.getData().get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
