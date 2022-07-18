package com.xevensolutions.baseapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.xevensolutions.baseapp.R;
import com.xevensolutions.baseapp.activities.BaseActivity;
import com.xevensolutions.baseapp.interfaces.BaseFragmentListener;
import com.xevensolutions.baseapp.interfaces.BaseFragmentMethods;
import com.xevensolutions.baseapp.presenters.BaseFragmentView;
import com.xevensolutions.baseapp.utils.AlertUtils;
import com.xevensolutions.baseapp.utils.Constants;


public abstract class BaseFragment<B extends ViewBinding> extends Fragment implements BaseFragmentMethods,
        BaseFragmentView {

    public B binding;

    ProgressDialog progressDialog;


    /**
     * if some child fragment is requesting for some permissions and
     * baseFragmentToPassResults !=null, then the results will be passed to this fragment
     * instead of currentFragment of BaseActivity
     */
    BaseFragment baseFragmentToPassResults;
    private boolean dismissLoading;


    public void onBackPressed() {
        try {
            ((BaseActivity) getActivity()).onSuperBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void initViewModel() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (getActivity() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getActivity());
            else if (getParentFragment() instanceof BaseFragmentListener)
                setBaseFragmentListener((BaseFragmentListener) getParentFragment());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChildFragment(BaseFragment baseFragmentChild, int fragmentToLoadLayoutId) {
        loadChildFragment(baseFragmentChild, fragmentToLoadLayoutId, 0, 0, 0, 0);

    }

    public void loadChildFragment(BaseFragment baseFragmentChild, int layoutId, int enterAnim,
                                  int exitAnim, int popEnter, int popExit) {
        baseFragmentChild.setAllowEnterTransitionOverlap(true);
        getChildFragmentManager().beginTransaction().replace(layoutId,
                baseFragmentChild).setCustomAnimations(enterAnim,
                exitAnim, popEnter,
                popExit).commit();

    }


    public void showProgressBar(String progressBarText) {


        progressDialog.setMessage(progressBarText);
        if (!progressDialog.isShowing())
            progressDialog.show();
        /*fragmentDialogProgress = FragmentDialogProgress.newInstance(progressBarText);
        fragmentDialogProgress.show(getChildFragmentManager());*/
    }


    public void observeData() {

    }

    public void requestPermissions(BaseFragment requestingFragment,
                                   int requestCode, String[] permissions) {
        if (requestingFragment.isChildFragment())
            ((BaseFragment) getParentFragment()).baseFragmentToPassResults = requestingFragment;
        else
            this.baseFragmentToPassResults = null;
        ((BaseActivity) getActivity()).requestPermissions(requestCode, permissions);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding = getViewBinding();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        initViewModel();
        setListeners();

        if (getArguments() != null)
            receiveExtras(getArguments());

        onCreateView(binding.getRoot());

        return binding.getRoot();
    }

    public abstract B getViewBinding();

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        observeData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (baseFragmentToPassResults != null)
            baseFragmentToPassResults.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Whenever you set some listeners on some views .e.g do set those listeners in this method,
     * so that it could be easy to find where the listeners are set instead of scrolling through
     * the whole file.
     */
    public void setListeners() {


    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == R.anim.slide_in_right) {
            ViewCompat.setTranslationZ(getView(), 1f);
        } else {
            ViewCompat.setTranslationZ(getView(), 0f);
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }


    public void onCreateView(View view) {
    }

    @Override
    public void showLoading(String message) {
        showProgressBar(message);
    }

    @Override
    public void dismissLoading() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }

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
    public void showNoData() {

    }


    @Override
    public void hideNoData() {

    }

    public void onDataCount(int count) {
        if (count == 0)
            showNoData();
        else
            hideNoData();
    }

    protected void updateListUIVisibility(boolean isListEmpty, RecyclerView listUI, View noRecordUI) {
        listUI.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
        noRecordUI.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
    }
}
