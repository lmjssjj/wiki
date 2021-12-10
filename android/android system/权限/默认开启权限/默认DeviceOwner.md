# 一键deviceowner

```java
 private boolean isAdb() {
        final int callingUid = mInjector.binderGetCallingUid();
        return callingUid == Process.SHELL_UID || callingUid == Process.ROOT_UID || getDPMPid(callingUid);
    }

	private boolean getDPMPid(int callingPid){
		boolean result = false;
		try {
            int pid = Settings.Secure.getInt(mContext.getContentResolver(), "dpmpid");
			Log.v("lmjssjj","pid:"+pid);
			Log.v("lmjssjj","callingPid:"+callingPid);
			result = (pid == callingPid);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
		Log.v("lmjssjj","result:"+result);
		return result;
	}


/**
     * 设置DeviceOwner
     * packageName: 应用程序包名
     * policyReceiver: 继承了DeviceAdminReceiver的类
     * deviceUserName: 为DeviceOwner设置一个名字，如不设置传null
     * return
     * true:  设置成功
     * false: 设置失败
     */
    public boolean setDeviceOwner(String packageName, String policyReceiver, String deviceUserName) {

        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 1. 保存本应用的进程号，在设置DeviceOwner时的权限检测中取出，视为Shell、Root。
        saveDPMPid(android.os.Process.myPid());
        // 2. 将包名和类名转换为ComponentName
        ComponentName component = new ComponentName(packageName, policyReceiver);
        // 3. 通过ComponentName获取ActivityInfo，如果为空，说明参数传递有误，系统中根本不存在这个应用，直接返回
        ActivityInfo ai = null;
        try {
            ai = getPackageManager().getReceiverInfo(component,
                    PackageManager.GET_META_DATA |
                            PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS |
                            PackageManager.MATCH_DIRECT_BOOT_UNAWARE |
                            PackageManager.MATCH_DIRECT_BOOT_AWARE);
        } catch (PackageManager.NameNotFoundException e) {
            Log.v("lmjssjj", ("Unable to load component: " + component));
            return false;
        }
        // 4. 调用DevicePolicyManager的现有方法setActiveAdmin(需要系统级应用有权限)
        manager.setActiveAdmin(component, true /*refreshing*/);
        // 5. 调用DevicePolicyManager的setDeviceOwner接口
        boolean result = false;
        result = manager.setDeviceOwner(component, deviceUserName);
        // Need to remove the admin that we just added.
        manager.removeActiveAdmin(component);
        if (result) {
            manager.setUserProvisioningState(DevicePolicyManager.STATE_USER_SETUP_FINALIZED, android.os.UserHandle.myUserId());
            Log.v("lmjssjj", ("Success: Device owner set to package " + component));
            Log.v("lmjssjj", "Active admin set to component " + component.toShortString());
        }
        return result;
    }


    private void saveDPMPid(int myPid) {
        Log.v("lmjssjj","save pid:"+myPid);
        Settings.Secure.putInt(getContentResolver(), "dpmpid", myPid);
        try {
            int pid = Settings.Secure.getInt(getContentResolver(), "dpmpid");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
```

# 另一种方式

```
拷贝文件到system目录
#deviceowner
PRODUCT_COPY_FILES += device/mediateksample/k61v1_32_bsp_hdp/device_owner_2.xml:/system/device_owner_2.xml
PRODUCT_COPY_FILES += device/mediateksample/k61v1_32_bsp_hdp/device_policies.xml:/system/device_policies.xml

在\system\core\rootdir\init.rc(此目录每次重启都会执行不能在这个目录下修改，应该在device目录下)  
（on post-fs-data） 里面复制文件到/data分区 并设置权限
	#deviceowner
	copy /system/device_owner_2.xml /data/system/device_owner_2.xml
	chmod 0600 /data/system/device_owner_2.xml
	chown system system /data/system/device_owner_2.xml
	copy /system/device_policies.xml /data/system/device_policies.xml
	chmod 0600 /data/system/device_policies.xml
	chown system system /data/system/device_policies.xml

可以通过预置apk方式将文件预置到指定位置
示例：
```

[]: ./deviceowner

