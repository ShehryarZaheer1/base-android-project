package com.xevensolutions.baseapp.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonSyntaxException;
import com.xevensolutions.baseapp.MyBaseApp;
import com.xevensolutions.baseapp.R;

import com.xevensolutions.baseapp.fragments.BaseFragment;
import com.xevensolutions.baseapp.presenters.BaseFragmentView;
import com.xevensolutions.baseapp.utils.AlertUtils;
import com.xevensolutions.baseapp.utils.CacheManager;
import com.xevensolutions.baseapp.utils.Constants;
import com.xevensolutions.baseapp.utils.GenericUtils;


import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;


public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity implements BaseFragmentView {


    public B binding;


    public abstract B getViewBinding();


    public void toggleToolbarVisibility(boolean isVisible) {
        if (getSupportActionBar() == null)
            return;

        if (isVisible)
            getSupportActionBar().show();
        else
            getSupportActionBar().hide();
    }


    public void showBackArrow() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void handleBackArrowClick(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

    }

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(BaseActivity.this);

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {
    }

    protected void onHideKeyboard() {
    }

    protected void attachKeyboardListeners(ViewGroup rootLayout) {
        if (keyboardListenersAttached) {
            return;
        }

        this.rootLayout = rootLayout;

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }


    public BaseFragment currentFragment;
    private Bundle arguments;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        beforeOnCreate();
        super.onCreate(savedInstanceState);
        binding = getViewBinding();

        setContentView(binding.getRoot());


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        progressDialog = new ProgressDialog(this);
        arguments = getIntent().getExtras();
        if (arguments != null)
            receiveExtras(arguments);


        setListeners();


        initViewModel();
        observeData();
    }

    protected void beforeOnCreate() {

    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        //setLocaleLanguage();
    }

   /* private void setLocaleLanguage() {

        if (CacheManager.getAppLanguage() != null) {

            Locale locale = new Locale(CacheManager.getAppLanguage());
            Configuration config = this.getResources().getConfiguration();
            config.setLocale(locale);
            this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());


            //Base App Locale...
            Locale baseLocale = new Locale(CacheManager.getAppLanguage());
            Configuration baseConfig = MyBaseApp.getContext().getResources().getConfiguration();
            baseConfig.setLocale(baseLocale);
            MyBaseApp.getContext().getResources().updateConfiguration(baseConfig, MyBaseApp.getContext().getResources().getDisplayMetrics());

        }

    }*/

    @Override
    protected void onStop() {
        super.onStop();

    }


    public void observeData() {

    }

    public void initViewModel() {

    }


    public void setListeners() {


    }


    public void showProgressBar(String progressBarText) {


        if (progressBarText == null)
            progressBarText = "Loading";
        showProgressDialog(progressBarText);



        /*fragmentDialogProgress = FragmentDialogProgress.newInstance(progressBarText);
        fragmentDialogProgress.show(getChildFragmentManager());*/
    }

    public void showProgressDialog(String text) {

        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(text);
            progressDialog.show();
        }
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
            AlertUtils.showSuccessErrorAlert(this, false, error, shouldEndActivity, -1, showToast);
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
            AlertUtils.showSuccessErrorAlert(this, true, successMessage, shouldEndActivity, requestCode,
                    showToast);
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void onTokenExpired() {

    }

    /**
     * returns the toolbar title to be displayed in the toolbar
     *
     * @return
     */

    public abstract int getActivityName();


    /**
     * This method checks that whether all the permission required are already allowed or not
     * if already allowed it will return empty arraylist otherwise arraylist with required
     * permissions
     *
     * @param permissions permissions to check allowance for
     * @return list of not allowed permissions
     */
    public ArrayList<String> checkPermissions(String[] permissions) {
        ArrayList<String> permissionsNotAllowed = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(BaseActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsNotAllowed.add(permission);
        }
        return permissionsNotAllowed;

    }

    public void requestPermissions(int requestCode, String[] permissions) {

        ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);

    }


    /**
     * if you are receiving extras from an activity through intent, Please get those extras
     * in this method. This method is called in the base Activity onCreate. Get all your arguments
     * by the provided arguments variable passed as an argument
     *
     * @param arguments get your data from this variable e.g. arguments.getSringExtra("your code");
     */
    public abstract void receiveExtras(Bundle arguments);


    public void changeFragment(BaseFragment baseFragment, int layoutId, boolean allowSameFragmentOverlap,
                               boolean addToBackstack) {


        changeFragment(baseFragment, layoutId, allowSameFragmentOverlap, addToBackstack, 0, 0, 0, 0);

    }

    public void changeFragment(BaseFragment baseFragment, int layoutId, boolean allowSameFragmentOverlap,
                               boolean addToBackstack, int enterAnim, int exitAnim
            , int popEnter, int popExit) {


        if (!allowSameFragmentOverlap &&
                currentFragment != null &&
                currentFragment.getFragmentName() > -1
                && baseFragment.getFragmentName() > -1 && getString(currentFragment.getFragmentName()).equals(getString(baseFragment.getFragmentName())))
            return;

        String name = "";
        if (baseFragment.getFragmentName() > -1)
            name = getString(baseFragment.getFragmentName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        enterAnim,
                        exitAnim,
                        popEnter,
                        popExit).
                        replace(layoutId, baseFragment, name);

        if (addToBackstack)
            fragmentTransaction.
                    addToBackStack(name);
        fragmentTransaction.commit();

        getSupportFragmentManager().executePendingTransactions();
        this.currentFragment = baseFragment;

    }


    public BaseFragment getLatestFragmentFromBackStack() {
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (index < 0 || getSupportFragmentManager().getBackStackEntryCount() == 0)
            return null;
        else {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            return baseFragment;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (currentFragment != null)
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onNoInternet() {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (currentFragment != null)
            currentFragment.onBackPressed();
        else
            onSuperBackPressed();

    }


    public void onSuperBackPressed() {


        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            currentFragment = getLatestFragmentFromBackStack();
        } else
            finish();


    }

    @Override
    public void showNoData() {

    }

    @Override
    public void hideNoData() {

    }

    public void gotoActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void showToast(String error) {

    }
}
