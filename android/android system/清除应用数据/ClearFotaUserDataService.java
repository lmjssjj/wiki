package com.nuumobile.settings;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.content.pm.IPackageDataObserver;
import com.nuumobile.aidl.*;

import androidx.annotation.Nullable;

/**
 * @author lmjssjj
 * @date created date 2020/5/29 13:00
 * @describe
 */
public class ClearFotaUserDataService extends Service {


    private final IClearAidlInterface.Stub mBinder = new IClearAidlInterface.Stub() {
        @Override
        public void clearFotaUserData(String packageName) throws RemoteException {
            ActivityManager am = (ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE);
            boolean res = am.clearApplicationUserData(packageName, 
				new ClearUserDataObserver());
            Log.v("lmjssjj", "clearUserData:" + res);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("lmjssjj", "service onBind");
        return mBinder;
    }

    class ClearUserDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            Log.v("lmjssjj", "clearUserData:" + packageName + "-" + succeeded);
        }
    }
}
