package com.xevensolutions.baseapp.utils;

import android.os.CountDownTimer;

import com.xevensolutions.baseapp.interfaces.CountDownTimerListener;


public class MyCountDown extends CountDownTimer {

    CountDownTimerListener countDownTimerListener;


    boolean isStopped;

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public boolean isStopped() {
        return isStopped;
    }

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDown(long millisInFuture, long countDownInterval, CountDownTimerListener countDownTimerListener) {
        super(millisInFuture, countDownInterval);
        this.countDownTimerListener = countDownTimerListener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (isStopped)
            return;
        try {
            countDownTimerListener.onTick(millisUntilFinished);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onFinish() {
        if (isStopped)
            return;
        try {
            countDownTimerListener.onFinish();
            isStopped = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
