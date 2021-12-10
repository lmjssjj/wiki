# DefaultPermissionGrantPolicy

frameworks\base\services\core\java\com\android\server\pm\permission\DefaultPermissionGrantPolicy.java

给应用默认开启权限

## 初始化

```java
//frameworks\base\services\core\java\com\android\server\pm\permission\PermissionManagerService.java
PermissionManagerService() {
      mDefaultPermissionGrantPolicy = new DefaultPermissionGrantPolicy(
                context, mHandlerThread.getLooper(), this);
}
```

## 读取默认配置权限文件

```java
File dir = new File(Environment.getRootDirectory(), "etc/default-permissions");
dir = new File(Environment.getVendorDirectory(), "etc/default-permissions");
dir = new File(Environment.getOdmDirectory(), "etc/default-permissions");
dir = new File(Environment.getProductDirectory(), "etc/default-permissions");
dir = new File(Environment.getProductServicesDirectory(),"etc/default-permissions");
```

```xml
<exceptions>
  <exception
    package="com.google.android.apps.restore"
    sha256-cert-digest="56:BE:13:2B:78:06:56:FE:24:44:CD:34:32:6E:B5:D7:AA:C9:1D:20:96:AB:F0:FE:67:3A:99:27:06:22:EC:87">
    <!-- External storage -->
    <permission name="android.permission.READ_EXTERNAL_STORAGE" fixed="false"/>
    <permission name="android.permission.WRITE_EXTERNAL_STORAGE" fixed="false"/>
    <!-- Contacts -->
    <permission name="android.permission.READ_CONTACTS" fixed="false"/>
    <permission name="android.permission.WRITE_CONTACTS" fixed="false"/>
  </exception>
<exceptions>
```

## 应用配置文件

```java
com.android.server.SystemServer#startOtherServices
->com.android.server.pm.PackageManagerService#systemReady
->
private static final int[] EMPTY_INT_ARRAY = new int[0];
int[] grantPermissionsUserIds = EMPTY_INT_ARRAY;
systemReady{
	for (int userId : grantPermissionsUserIds) {
         mDefaultPermissionPolicy.grantDefaultPermissions(userId);
    }
}
```

## 默认授予的某些应用权限

```java
LocationManagerService()
-> {packageManagerInternal.setLocationExtraPackagesProvider(
                userId -> mContext.getResources().getStringArray(
                      com.android.internal.R.array.config_locationExtraPackageNames));}
packageManagerInternal.setLocationExtraPackagesProvider
->mDefaultPermissionPolicy.setLocationExtraPackagesProvider(provider);    

```

```java
//DefaultPermissionGrantPolicy
public void setLocationExtraPackagesProvider(PackagesProvider provider) {
        synchronized (mLock) {
            mLocationExtraPackagesProvider = provider;
        }
}

grantDefaultSystemHandlerPermissions->
String[] locationPackageNames = (locationPackagesProvider != null)
                ? locationPackagesProvider.getPackages(userId) : null;
// Location
if (locationPackageNames != null) {
            for (String packageName : locationPackageNames) {
                grantPermissionsToSystemPackage(packageName, userId,
                        CONTACTS_PERMISSIONS, CALENDAR_PERMISSIONS, MICROPHONE_PERMISSIONS,
                        PHONE_PERMISSIONS, SMS_PERMISSIONS, CAMERA_PERMISSIONS,
                        SENSORS_PERMISSIONS, STORAGE_PERMISSIONS);
                grantSystemFixedPermissionsToSystemPackage(packageName, userId,
                        ALWAYS_LOCATION_PERMISSIONS, ACTIVITY_RECOGNITION_PERMISSIONS);
            }
}
```

