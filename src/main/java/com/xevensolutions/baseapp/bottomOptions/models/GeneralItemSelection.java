package com.xevensolutions.baseapp.bottomOptions.models;

import java.util.ArrayList;

public class GeneralItemSelection implements ItemSelection {

    int id;
    int icon;
    String title;

    public GeneralItemSelection(int id, int icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public ArrayList<? extends ItemSelection> getChildNodes() {
        return null;
    }

    @Override
    public int getIconRes() {
        return icon;
    }
}
