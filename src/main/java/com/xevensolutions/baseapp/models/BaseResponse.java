package com.xevensolutions.baseapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {

    @SerializedName("succeeded")
    boolean succeeded;
    @SerializedName("message")
    String message;
    @SerializedName("httpStatusCode")
    int status;
    @SerializedName("data")
    T data;
    @SerializedName("totalData")
    int totalData;

    public BaseResponse(){

    }


    public BaseResponse(final boolean succeeded, final String message, final int status, final T data) {
        this.succeeded = succeeded;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public BaseResponse(final T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return data != null;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public int getTotalData() {
        return totalData;
    }
}
