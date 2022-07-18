package com.xevensolutions.baseapp.bottomOptions.models;

import com.xevensolutions.baseapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public interface ItemSelection extends Serializable {

    long getId();

    String getTitle();

    default String getUnit() {
        return "";
    }

    default String getIcon() {
        return "";
    }

    default int getIconRes() {
        return 0;
    }

    default String getHint() {
        return "";
    }

    default ArrayList<? extends ItemSelection> getChildNodes()  {
        return null;
    }

    default boolean isSelected() {
        return false;
    }

    default void setSelected(boolean isSelected) {

    }

    default int getItemBackgroundColor() {
        return R.color.white;
    }
}
