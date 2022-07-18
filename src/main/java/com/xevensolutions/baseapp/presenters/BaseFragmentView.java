package com.xevensolutions.baseapp.presenters;

public interface BaseFragmentView {
    default void showLoading(String message) {

    }

    default void dismissLoading() {

    }

    default void showError(String error, boolean shouldEndActivity, boolean showToast) {

    }

    default void showSuccessMessage(String message, boolean shouldEndActivity, int requestCode, boolean showToast) {

    }

    default void onTokenExpired() {

    }

    default void onFinishWithError(String error) {

    }

    default void onNoInternet() {

    }

    default void showNoData() {

    }

    default void hideNoData() {

    }

    default String getNoDataText() {
        return "No data found";
    }

    default void exitActivity() {

    }

    default void showToast(String error) {

    }
}
