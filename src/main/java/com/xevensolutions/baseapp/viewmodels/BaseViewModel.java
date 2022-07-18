package com.xevensolutions.baseapp.viewmodels;

import android.content.Context;
import android.widget.Toast;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.google.gson.Gson;
import com.xevensolutions.baseapp.interactors.BaseInteractor;
import com.xevensolutions.baseapp.presenters.BaseFragmentView;

import java.util.List;


public abstract class BaseViewModel<T> extends ViewModel implements BaseInteractor {

    public BaseFragmentView baseFragmentView;
    public T view;
    //    public MutableLiveData<T> data;
    public Context context;

    public T getView() {
        return (T) baseFragmentView;
    }

    public void setView(T view) {
        this.view = view;
        this.baseFragmentView = (BaseFragmentView) view;
    }

    public void exitActivity(Integer status) {
        if (status == 1)
            baseFragmentView.exitActivity();
    }

    public BaseViewModel() {
//        data = new MutableLiveData<>();
        this.context = getContext();
    }

    public abstract Context getContext();

    public String getString(int id) {
        return context.getString(id);
    }

    public void setBaseFragmentView(BaseFragmentView baseFragmentView) {
        this.baseFragmentView = baseFragmentView;
    }

    @Override
    public void onSuccess() {
        baseFragmentView.dismissLoading();
    }

    @Override
    public void onFailure(String error, Integer status) {
        if (baseFragmentView == null)
            return;
        baseFragmentView.dismissLoading();
//        baseFragmentView.showError(error, false);
        baseFragmentView.showToast(error);

//        if (status == 401){
//            baseFragmentView.gotoLoginScreen();
//            return;
//        }

    }


//    public MutableLiveData<T> getData() {
//        return data;
//    }


}
