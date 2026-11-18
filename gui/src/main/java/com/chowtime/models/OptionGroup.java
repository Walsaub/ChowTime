package com.chowtime.models;

import java.util.ArrayList;
import java.util.List;

public class OptionGroup {
    private final int id;
    private final String groupName;
    private final int minSelections;
    private final int maxSelections;
    private final List<MenuOption> options;

    public OptionGroup(int id, String groupName, int minSelections, int maxSelections) {
        this.id = id;
        this.groupName = groupName;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
        this.options = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getMinSelections() {
        return minSelections;
    }

    public int getMaxSelections() {
        return maxSelections;
    }

    public List<MenuOption> getOptions() {
        return options;
    }

    public void addOption(MenuOption option){
        options.add(option);
    }
}
