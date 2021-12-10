package com.lmjssjj.sytemutils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.telecom.DefaultDialerManager;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class SetDefaultDialerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_default_dialer);
    }

    public void setDefault(View view) {
//        UserHandle user = android.os.Process.myUserHandle();
//        boolean b = setDefaultDialerApplication(this, getPackageName(), user.getIdentifier());
//        Toast.makeText(this, b + "", Toast.LENGTH_SHORT).show();
        set();
    }

    public boolean setDefaultDialerApplication(Context context, String packageName,
                                               int userId) {
        return DefaultDialerManager.setDefaultDialerApplication(context, "com.lmjssjj.apitestdemo", userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void set() {
        RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
        Executor executor = getMainExecutor();
        Consumer<Boolean> callback = successful -> {
            if (successful) {
            } else {

            }
            Toast.makeText(this, successful + "", Toast.LENGTH_SHORT).show();
        };
        int flags = 0;
        UserHandle user = android.os.Process.myUserHandle();
        roleManager.addRoleHolderAsUser(RoleManager.ROLE_DIALER, "com.lmjssjj.apitestdemo", flags, user, executor, callback);
    }

    public void clear(){
        RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
        Executor executor = getMainExecutor();
        Consumer<Boolean> callback = successful -> {
            if (successful) {
            } else {

            }
            Toast.makeText(this, successful + "", Toast.LENGTH_SHORT).show();
        };
        UserHandle user = android.os.Process.myUserHandle();
        roleManager.clearRoleHoldersAsUser(RoleManager.ROLE_DIALER, 0, user, executor, callback);
    }

    public void clearDefault(View view) {
        clear();
    }
}