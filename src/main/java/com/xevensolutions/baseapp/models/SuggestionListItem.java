package com.xevensolutions.baseapp.models;

import java.io.Serializable;

public interface SuggestionListItem extends Serializable {


    String getSuggestion();

    default int getId() {
        return 0;
    }

    ;

    default String getTitle() {
        return null;
    }

    default String getProfile() {
        return null;
    }


    default boolean isChecked() {
        return false;
    }

    default void setChecked(boolean isChecked) {

    }

    default String getTimeAgo() {
        return "";
    }

    default String getUUid() {
        return "";
    }
}
