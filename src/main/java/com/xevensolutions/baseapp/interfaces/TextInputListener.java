package com.xevensolutions.baseapp.interfaces;


import com.xevensolutions.baseapp.models.SuggestionListItem;

public interface TextInputListener {

    void onTextEntered(String text, SuggestionListItem selectedOption);
}
