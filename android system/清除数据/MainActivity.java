package com.nuumobile.aidl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private IClearAidlInterface mRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean b = bindService(getRemoteIntent(), mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.v("lmjssjj", "isbind:" + b);
    }

    public void clear(View view) {
        Intent intent = new Intent("com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA");
        ComponentName componentName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaDataReceiver");
        intent.setComponent(componentName);
        intent.putExtra("package_name","com.android.quicksearchbox");
        sendBroadcast(intent);


        Intent service = new Intent();
        ComponentName serviceName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaUserDataService_");
        service.setComponent(serviceName);
        service.putExtra("package_name","com.android.quicksearchbox");
        startService(service);


        if (mRemoteService != null)
            try {
                mRemoteService.clearFotaUserData("com.nuumobile.agingtest");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    private Intent getRemoteIntent() {
        Intent service = new Intent();
        service.addCategory(Intent.CATEGORY_DEFAULT);
        ComponentName componentName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaUserDataService");
        service.setComponent(componentName);
        return service;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v("lmjssjj", "client conn remote succce");
            mRemoteService = IClearAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
            Log.v("lmjssjj", "client->onServiceDisconnected");
        }
    };
}
