方式一：Called by the device owner or profile owner to clear application user data of a given package.
  mDevicePolicyManager.clearApplicationUserData(
                mAdminComponentName,
                packageName,);


在Settings添加清除应用数据代码
方式二：广播方式
1、在设置应用里面创建一个BroadcastReceiver，通过这个广播来清除数据 参考文件ClearFotaDataReceiver.java
2、在设置的AndroidManifest.xml里面添加：
 <receiver
            android:name="com.nuumobile.settings.ClearFotaDataReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA" />
            </intent-filter>
        </receiver>

3、在需要清除数据时通过发送广播来清除

	Intent intent = new Intent("com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA");
        ComponentName componentName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaDataReceiver");
        intent.setComponent(componentName);
        intent.putExtra("package_name","com.android.quicksearchbox");
        sendBroadcast(intent);



方式三：服务方式
1、在设置应用里面创建一个BroadcastReceiver，通过这个广播来清除数据 参考文件ClearFotaUserDataService_.java
2、在设置的AndroidManifest.xml里面添加：
  <service android:name="com.nuumobile.settings.ClearFotaUserDataService_"
            android:exported="true"
            android:enabled="true"/>

3、在需要清除数据时通过打开服务清除
 	Intent service = new Intent();
        ComponentName serviceName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaUserDataService_");
        service.setComponent(serviceName);
        service.putExtra("package_name","com.android.quicksearchbox");
        startService(service);



方式四：aidl方式
1、在设置创建aidl文件 参考 IClearAidlInterface.aidl
2、在设置里面创建对应的服务 参考 ClearFotaUserDataService.java
3、在设置的AndroidManifest.xml里面添加：
  <service android:name="com.nuumobile.settings.ClearFotaUserDataService"
            android:exported="true"
            android:enabled="true"/>
4、在设置的Android.mk 对应位置 添加aidl文件如下：
  
   LOCAL_SRC_FILES += $(call all-Iaidl-files-under, src/com/nuumobile/aidl)

5、在需要清除数据时通过aidl清除 使用参考MainActivity.java

  