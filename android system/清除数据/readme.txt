��ʽһ��Called by the device owner or profile owner to clear application user data of a given package.
  mDevicePolicyManager.clearApplicationUserData(
                mAdminComponentName,
                packageName,);


��Settings������Ӧ�����ݴ���
��ʽ�����㲥��ʽ
1��������Ӧ�����洴��һ��BroadcastReceiver��ͨ������㲥��������� �ο��ļ�ClearFotaDataReceiver.java
2�������õ�AndroidManifest.xml������ӣ�
 <receiver
            android:name="com.nuumobile.settings.ClearFotaDataReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA" />
            </intent-filter>
        </receiver>

3������Ҫ�������ʱͨ�����͹㲥�����

	Intent intent = new Intent("com.nuumobile.intent.action.NUU_CLEAR_FOTA_DATA");
        ComponentName componentName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaDataReceiver");
        intent.setComponent(componentName);
        intent.putExtra("package_name","com.android.quicksearchbox");
        sendBroadcast(intent);



��ʽ��������ʽ
1��������Ӧ�����洴��һ��BroadcastReceiver��ͨ������㲥��������� �ο��ļ�ClearFotaUserDataService_.java
2�������õ�AndroidManifest.xml������ӣ�
  <service android:name="com.nuumobile.settings.ClearFotaUserDataService_"
            android:exported="true"
            android:enabled="true"/>

3������Ҫ�������ʱͨ���򿪷������
 	Intent service = new Intent();
        ComponentName serviceName = new ComponentName("com.android.settings", "com.nuumobile.settings.ClearFotaUserDataService_");
        service.setComponent(serviceName);
        service.putExtra("package_name","com.android.quicksearchbox");
        startService(service);



��ʽ�ģ�aidl��ʽ
1�������ô���aidl�ļ� �ο� IClearAidlInterface.aidl
2�����������洴����Ӧ�ķ��� �ο� ClearFotaUserDataService.java
3�������õ�AndroidManifest.xml������ӣ�
  <service android:name="com.nuumobile.settings.ClearFotaUserDataService"
            android:exported="true"
            android:enabled="true"/>
4�������õ�Android.mk ��Ӧλ�� ���aidl�ļ����£�
  
   LOCAL_SRC_FILES += $(call all-Iaidl-files-under, src/com/nuumobile/aidl)

5������Ҫ�������ʱͨ��aidl��� ʹ�òο�MainActivity.java

  