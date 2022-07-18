package com.xevensolutions.baseapp.bottomOptions.UI.fragments;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xevensolutions.baseapp.fragments.BaseFragment;
import com.xevensolutions.baseapp.interfaces.BaseFragmentListener;
import com.xevensolutions.baseapp.interfaces.BaseFragmentMethods;
import com.xevensolutions.baseapp.presenters.BaseFragmentView;
import com.xevensolutions.baseapp.utils.AlertUtils;
import com.xevensolutions.baseapp.utils.Constants;

import butterknife.ButterKnife;

public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment implements BaseFragmentMethods
        , BaseFragmentView {

    Bundle bundle;
    private ProgressDialog progressDialog;

    public abstract void show(FragmentManager fragmentManager);


    public void loadFragment(BaseFragment baseFragmentChild, int fragmentToLoadLayoutId) {

        baseFragmentChild.setAllowEnterTransitionOverlap(true);
        getChildFragmentManager().beginTransaction().replace(fragmentToLoadLayoutId,
                baseFragmentChild).commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bundle = getArguments();
            receiveExtras(bundle);
        }
    }

    @Override
    public void onNoInternet() {

    }

    public int getDialogWidth() {
        return MATCH_PARENT;
    }

    public int getDialogHeight() {
        return WRAP_CONTENT;
    }


    public boolean isCancelable() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (isChildFragment()) {
            if (getParentFragment() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getParentFragment());
            else if (getActivity() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getActivity());
        } else {
            if (getActivity() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getActivity());
            else if (getParentFragment() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getParentFragment());
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
/*
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.getWindow()
                .getAttributes().windowAnimations = R.style.MyBottomSheetAnimation;
        return bottomSheetDialog;
*/
    }

    @Override
    public void onStart() {
        super.onStart();
/*        int width = getDialogWidth();
        int height = getDialogHeight();
        getDialog().getWindow().setLayout(width, height);*/
        getDialog().setCancelable(isCancelable());
        /*getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.BottomSheetAnimation;
*/
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutResource() == 0) {
            throw new NullPointerException("No layout id returned in getRootLayoutId to inflate");

        } else {
            View view = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, view);
            progressDialog = new ProgressDialog(getActivity());

            //getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            onCreateView(view);
            setListeners();
            observeData();
            return view;
        }

    }

    protected void setListeners() {

    }


    public void onCreateView(View view) {

    }

    public void observeData() {

    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

    }


    public void showProgressBar(String progressBarText) {


        progressDialog.setMessage(progressBarText);
        if (!progressDialog.isShowing())
            progressDialog.show();

        /*fragmentDialogProgress = FragmentDialogProgress.newInstance(progressBarText);
        fragmentDialogProgress.show(getChildFragmentManager());*/
    }

    @Override
    public void showLoading(String message) {
        showProgressBar(getLoadingText());
    }

    @Override
    public void dismissLoading() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
        /*if (fragmentDialogProgress != null) {
            try {
                fragmentDialogProgress.dismiss();
                fragmentDialogProgress = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                this.dismissLoading = true;
            }
        }*/
    }


    @Override
    public void showError(String error, boolean shouldEndActivity, boolean showToast) {
        dismissLoading();
        try {
            AlertUtils.showSuccessErrorAlert(getActivity(), false, error, shouldEndActivity, -1, showToast);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    public String getLoadingText() {
        return "Loading";
    }

    @Override
    public void showSuccessMessage(String message, boolean shouldEndActivity, int requestCode, boolean showToast) {

        String successMessage = null;
        if (message == null || message.isEmpty()) successMessage = Constants.GenericSuccess;
        else successMessage = message;
        try {
            AlertUtils.showSuccessErrorAlert(getActivity(), true, successMessage, shouldEndActivity, requestCode,
                    showToast);
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void onTokenExpired() {

    }

    public abstract int getLayoutResource();
}
