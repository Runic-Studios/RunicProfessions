package com.runicrealms.plugin.professions.model;

public enum ProfessionField {

    PROF_NAME("profName"),
    PROF_EXP("profExp"),
    PROF_LEVEL("profLevel");

    private final String field;

    ProfessionField(String field) {
        this.field = field;
    }

    /**
     * Returns the corresponding ProfessionField from the given string version
     *
     * @param field a string matching a constant
     * @return the constant
     */
    public static ProfessionField getFromFieldString(String field) {
        for (ProfessionField professionField : ProfessionField.values()) {
            if (professionField.getField().equalsIgnoreCase(field))
                return professionField;
        }
        return null;
    }

    public String getField() {
        return field;
    }
}
