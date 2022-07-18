package com.xevensolutions.baseapp.bottomOptions.interfaces;


import com.xevensolutions.baseapp.bottomOptions.models.ItemSelection;
import com.xevensolutions.baseapp.interfaces.BaseFragmentListener;

import java.util.ArrayList;

public interface EmployeeSelectorListener extends BaseFragmentListener {

    void onEmployeeSelected(int requestingViewId, ItemSelection suggestionListItem);

    default void onMultipleItemsSelected(int requstingViewId, ArrayList<? extends ItemSelection> selectedItems) {

    }

    default void onAddNewTapped(int requestViewId) {

    }
}
