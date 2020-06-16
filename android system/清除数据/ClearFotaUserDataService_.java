package com.nuumobile.settings;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author lmjssjj
 * @date created date 2020/5/29 13:00
 * @describe
 */
public class ClearFotaUserDataService_ extends IntentService {

    public ClearFotaUserDataService_() {
        super("ClearFotaUserDataService_");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        clear(intent);
    }

    private void clear(Intent intent) {
        String packageName = intent.getStringExtra("package_name");
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(packageName, null);
        Log.v("lmjssjj", "ClearFotaUserDataService_:" + res);
    }

}

