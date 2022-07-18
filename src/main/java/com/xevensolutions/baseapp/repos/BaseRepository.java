package com.xevensolutions.baseapp.repos;

import android.util.Log;

import com.google.gson.Gson;
import com.xevensolutions.baseapp.interactors.BaseInteractor;
import com.xevensolutions.baseapp.interfaces.ApiCallback;
import com.xevensolutions.baseapp.models.BaseResponse;


import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;


public class BaseRepository {


    public BaseInteractor interactor;


    public BaseRepository(BaseInteractor baseInteractor) {
        this.interactor = baseInteractor;
    }


    public <E> void makeApiCall(Single<E> single, ApiCallback<E> apiCallback) {

        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((userBaseResponse, throwable)
                -> {
            if (apiCallback == null)
                return;

            onApiResponse(userBaseResponse, apiCallback, throwable);
        });
    }

    public <E> void onApiResponse(E userBaseResponse, ApiCallback<E> apiCallback, Throwable throwable) {
        if (userBaseResponse != null) {
            if (userBaseResponse instanceof BaseResponse) {
                if (((BaseResponse) userBaseResponse).getStatus() != 200) {
                    apiCallback.onFailure(((BaseResponse) userBaseResponse).getMessage(),
                            ((BaseResponse<?>) userBaseResponse).getStatus());
                    return;
                }
            }

            try {
                BaseResponse baseResponse = (BaseResponse) userBaseResponse;
                apiCallback.onSuccess(userBaseResponse, baseResponse.getMessage(), baseResponse.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (throwable != null) {
            apiCallback.onFailure(throwable.getMessage(), -1);
        }
    }


}
