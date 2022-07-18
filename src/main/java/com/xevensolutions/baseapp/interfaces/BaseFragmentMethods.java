package com.xevensolutions.baseapp.interfaces;

import android.content.Context;
import android.os.Bundle;

public interface BaseFragmentMethods {


    /**
     * This method makes sure that whether the fragment should appear when back button is pressed or not
     *
     * @return true if you want to add fragment to backstack otherwise false
     */
    default boolean addToBackStack() {
        return false;
    }

    public int getFragmentName();


    /**
     * @return returns true if this fragment is a child fragment otherwise false
     */
    default boolean isChildFragment() {
        return false;
    }

    ;

    /**
     * set the base fragment listener after getting in onAttach
     * please replace your basefragmentlistener with the one provided
     * in the arguments
     *
     * @param baseFragmentListener
     */
    default void setBaseFragmentListener(BaseFragmentListener baseFragmentListener) {

    }

    /**
     * if you are receiving extras from an activity through intent, Please get those extras
     * in this method. This method is called in the base Activity onCreate. Get all your arguments
     * by the provided arguments variable passed as an argument
     *
     * @param arguments get your data from this variable e.g. arguments.getSringExtra("your code");
     */
    default void receiveExtras(Bundle arguments) {

    }


}
