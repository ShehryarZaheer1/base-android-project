package com.xevensolutions.baseapp.utils;

import android.content.Context;

import com.google.gson.Gson;

public class CacheManager {

    private static final String APP_LANGUAGE = "application_language";
    Context context;
    TinyDB tinyDB;
    Gson gson;
    private static CacheManager instance = new CacheManager();


    public void setContext(Context context) {
        this.context = context;
        tinyDB = new TinyDB(context);
        gson = new Gson();
    }

    public static CacheManager getInstance() {
        return instance;
    }

    public static String getAppLanguage() {
        return getInstance().tinyDB.getString(APP_LANGUAGE);
    }

    public static void setAppLanguage(String locale) {
        getInstance().tinyDB.putString(APP_LANGUAGE, locale);
    }
}