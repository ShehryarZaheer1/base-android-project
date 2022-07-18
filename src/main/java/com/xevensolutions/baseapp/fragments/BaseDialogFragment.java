package com.xevensolutions.baseapp.fragments;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.xevensolutions.baseapp.interfaces.BaseFragmentMethods;

import java.util.Objects;

public abstract class BaseDialogFragment<B extends ViewBinding> extends DialogFragment implements BaseFragmentMethods {


    public B binding;


    public int getDialogWidth() {
        return MATCH_PARENT;
    }

    public int getDialogHeight() {
        return WRAP_CONTENT;
    }

    public int getWindowsAnimations() {
        return androidx.appcompat.R.style.Animation_AppCompat_Dialog;
    }


    public void initViewModel() {

    }

    public void observeData() {

    }

    @Override

    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog()).getWindow().getAttributes().windowAnimations = getWindowsAnimations();

        int width = getDialogWidth();
        int height = getDialogHeight();
        getDialog().getWindow().setLayout(width, height);
        getDialog().setCancelable(isCancelable());
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = getViewBinding();
        initViewModel();
        observeData();
        onCreateView(binding.getRoot());
        return binding.getRoot();
    }

    public void onCreateView(View view) {

    }

    public void changeFragment(BaseFragment baseFragment, int layout) {
        changeFragment(baseFragment, layout, 0, 0, 0, 0);
    }

    public void changeFragment(BaseFragment baseFragment, int layoutId,
                               int enterAnim, int exitAnim, int popEnter, int popExit) {

        String name = "";
        if (baseFragment.getFragmentName() > -1)
            name = getString(baseFragment.getFragmentName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction()
                .setCustomAnimations(
                        enterAnim,
                        exitAnim,
                        popEnter,
                        popExit).
                        replace(layoutId, baseFragment, name);


        fragmentTransaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveExtras(getArguments());
    }

    public abstract B getViewBinding();

    @Override
    public int getFragmentName() {
        return -1;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getFragmentName() > -1 ? getString(getFragmentName()) : "");
    }

}