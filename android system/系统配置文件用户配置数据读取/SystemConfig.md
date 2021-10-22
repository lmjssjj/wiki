# SystemConfig

## 读取文件

```
/system/etc/sysconfig
/system/etc/permissions
/vendor/etc/sysconfig
/vendor/etc/permissions
/oem/etc/sysconfig
/oem/etc/permissions
/odm/etc/sysconfig
/odm/etc/permissions
/product/etc/sysconfig
/product/etc/permissions
目录下的xml文件
```

## 解析保存文件配置

### etc/permissions/platform.xml

```xml
//这些是从系统配置文件中读取的权限—> gid映射。
//解析对应保存在 mPermissions
final ArrayMap<String, PermissionEntry> mPermissions = new ArrayMap<>();
<permission name="android.permission.BLUETOOTH_ADMIN" >
      <group gid="net_bt_admin" />
</permission>
```

```xml
以下标签将高级权限分配给特定的
          用户 ID。 这些用于允许特定的核心系统用户
          使用更高级别的框架执行给定的操作。 为了
          例如，我们为 shell 用户提供了多种权限
          因为这是运行 adb shell 的用户和开发人员以及
          其他人应该有一个相当开放的环境
          与系统交互。
内置uid ->权限映射
解析保存在mSystemPermissions
SparseArray<ArraySet<String>> mSystemPermissions = new SparseArray<>();
<assign-permission name="android.permission.MODIFY_AUDIO_SETTINGS" uid="media" />
```

```xml
在以前的API级别中添加的权限可能会分成几个权限。 这个对象描述了一个这样的分裂。  
保存在 mSplitPermissions
final ArrayList<SplitPermissionInfo> mSplitPermissions = new ArrayList<>();
<split-permission name="android.permission.ACCESS_FINE_LOCATION">
     <new-permission name="android.permission.ACCESS_COARSE_LOCATION" />
</split-permission>
<split-permission name="android.permission.READ_EXTERNAL_STORAGE"
                      targetSdk="29">
        <new-permission name="android.permission.ACCESS_MEDIA_LOCATION" />
</split-permission>
```

```xml
这些是从系统配置文件读取的内置共享库。 键是库名; 值是包含文件名和依赖项等信息的单个条目。
这是应用程序代码可链接的所有库的列表。  
保存在 mSharedLibraries
ArrayMap<String, SharedLibraryEntry> mSharedLibraries = new ArrayMap<>();
<library name="android.test.base"
            file="/system/framework/android.test.base.jar" />
<library name="android.test.mock"
            file="/system/framework/android.test.mock.jar"
            dependency="android.test.base" />
```

```xml
这些包被列入白名单，以便在省电模式下在后台运行，就像从配置文件中读取的那样。  
这些都是被列入白名单的标准套件，在省电模式下总是可以上网，即使它们不在前台。  
电池优化白名单
ArraySet<String> mAllowInPowerSave = new ArraySet<>();
<allow-in-power-save package="com.android.providers.downloads" />
```

```xml
这些包被列入白名单，以便能够在数据使用保存模式下在后台运行，就像从配置文件中读取一样。  
这些是被列入白名单的标准软件包，在数据模式下总能访问互联网，即使它们不在前台。 
ArraySet<String> mAllowInDataUsageSave = new ArraySet<>();
<allow-in-data-usage-save package="com.android.providers.downloads" />
```

```xml
这些包被列入了白名单，以便能够在节能模式下在后台运行(而不是从设备空闲模式下进入白名单)，从配置文件中读取。
ArraySet<String> mAllowInPowerSaveExceptIdle = new ArraySet<>();
<allow-in-power-save-except-idle package="com.android.providers.calendar" />
```

```xml
这些包被列入白名单，可以作为系统用户运行
ArraySet<String> mSystemUserWhitelistedApps = new ArraySet<>();
<system-user-whitelisted-app package="com.android.settings" />
```

```xml
这些包不应该以系统用户的身份运行
ArraySet<String> mSystemUserBlacklistedApps = new ArraySet<>();
<system-user-blacklisted-app package="com.android.wallpaper.livepicker" />
```

### etc/permissions/privapp-permissions-platform.xml

```xml
这个XML文件声明应该将哪些签名|特权权限授予平台附带的特权应用程序  
ArrayMap<String, ArraySet<String>> mPrivAppPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mPrivAppDenyPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mVendorPrivAppPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mVendorPrivAppDenyPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mProductPrivAppPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mProductPrivAppDenyPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mProductServicesPrivAppPermissions = new ArrayMap<>();
ArrayMap<String, ArraySet<String>> mProductServicesPrivAppDenyPermissions =
            new ArrayMap<>();

<privapp-permissions package="com.android.launcher3">
      <permission name="android.permission.WRITE_SECURE_SETTINGS"/>
</privapp-permissions>
```

### etc/sysconfig/hiddenapi-package-whitelist.xml

```xml
这个XML文件声明哪些系统应用程序应该从隐藏的API黑名单中豁免，即。  
哪些应用应该被允许访问整个私有API。  
 
只需要包含未使用平台证书签名的应用程序，就像使用平台签名的应用程序一样  
默认情况下证书被豁免。  

从私有API黑名单中豁免的包名  
ArraySet<String> mHiddenApiPackageWhitelist = new ArraySet<>();
<hidden-api-whitelisted-app package="com.android.statementservice" />
```

### etc/sysconfig/preinstalled-packages-platform.xml

```
https://source.android.com/devices/tech/config/preinstalled-packages?hl=zh-cn
```

### feature/unavailable-feature

```xml
这些是该设备支持的特性
ArrayMap<String, FeatureInfo> mAvailableFeatures = new ArrayMap<>();
<feature name="android.software.device_id_attestation" />
    
这些是设备不支持的功能;
ArraySet<String> mUnavailableFeatures = new ArraySet<>();

```

