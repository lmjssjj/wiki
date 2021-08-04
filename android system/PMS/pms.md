# Android 10 code

## 启动

```java
///frameworks/base/services/java/com/android/server/SystemServer.java
/**
     * The main entry point from zygote.
     */
    public static void main(String[] args) {
        new SystemServer().run();
    }
	private void startBootstrapServices() {
         mPackageManagerService = PackageManagerService.main(mSystemContext, installer,
                    mFactoryTestMode != FactoryTest.FACTORY_TEST_OFF, mOnlyCore);
    }
```

```java
///frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java
public static PackageManagerService main(Context context, Installer installer,
            boolean factoryTest, boolean onlyCore) {
        // Self-check for initial settings.
        PackageManagerServiceCompilerMapping.checkProperties();

        PackageManagerService m = new PackageManagerService(context, installer,
                factoryTest, onlyCore);
        m.enableSystemUserPackages();
        ServiceManager.addService("package", m);
        final PackageManagerNative pmn = m.new PackageManagerNative();
        ServiceManager.addService("package_native", pmn);
        return m;
    }
```

```java
public PackageManagerService(Context context, Installer installer,
        boolean factoryTest, boolean onlyCore) {
        ...
        //BOOT_PROGRESS_PMS_START
        EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_PMS_START,
                SystemClock.uptimeMillis());
 
        //BOOT_PROGRESS_PMS_SYSTEM_SCAN_START
        EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_PMS_SYSTEM_SCAN_START,
                    startTime);
        ...
        
       //BOOT_PROGRESS_PMS_DATA_SCAN_START
        if (!mOnlyCore) {
                EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_PMS_DATA_SCAN_START,
                        SystemClock.uptimeMillis());
        }
        ...
        //BOOT_PROGRESS_PMS_SCAN_END
        EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_PMS_SCAN_END,
                    SystemClock.uptimeMillis());
        ...
        //BOOT_PROGRESS_PMS_READY
        EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_PMS_READY,
                    SystemClock.uptimeMillis());
}
```

