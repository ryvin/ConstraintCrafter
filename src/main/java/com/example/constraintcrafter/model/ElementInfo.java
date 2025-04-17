package com.example.constraintcrafter.model;

import java.util.List;

public class ElementInfo {
    private final String name;
    private final int minOccurs;
    private final int maxOccurs;    // -1 = unbounded
    private final List<String> enumValues;

    public ElementInfo(String name, int minOccurs, int maxOccurs, List<String> enumValues) {
        this.name       = name;
        this.minOccurs  = minOccurs;
        this.maxOccurs  = maxOccurs;
        this.enumValues = enumValues;
    }

    public String       getName()       { return name; }
    public int          getMinOccurs()  { return minOccurs; }
    public int          getMaxOccurs()  { return maxOccurs; }
    public List<String> getEnumValues() { return enumValues; }
    public boolean      hasEnum()       { return !enumValues.isEmpty(); }
}
