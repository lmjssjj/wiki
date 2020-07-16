package com.nuumobile.settings;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ClearFotaDataReceiver extends BroadcastReceiver {

    public final static String NUU_CLEAR_FOTA_DATA = "com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
		Log.v("lmjssjj", action);
        if (action.equals(NUU_CLEAR_FOTA_DATA)){
			String packageName = intent.getStringExtra("package_name");
            ActivityManager am = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            boolean res = am.clearApplicationUserData(packageName, null);
			Log.v("lmjssjj", ""+res);
        }
    }
}