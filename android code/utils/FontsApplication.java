package com.nuumobile.fontsettings;

import android.app.Application;

public class FontsApplication extends Application {
    public static FontsApplication instance;

    public static FontsApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
