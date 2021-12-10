# DevicePolicyManager.java

```
frameworks/base/core/java/android/app/admin/DevicePolicyManager.java
```

## setApplicationRestrictions

```java
//Sets the application restrictions for a given target application running in the calling user.
@WorkerThread
public void setApplicationRestrictions(@Nullable ComponentName admin, String packageName,
            Bundle settings) {
     mService.setApplicationRestrictions(admin, mContext.getPackageName(), packageName,settings);
         
}
//frameworks/base/services/devicepolicy/java/com/android/server/devicepolicy/DevicePolicyManagerService.java
@Override
public void setApplicationRestrictions(ComponentName who, String callerPackage,
            String packageName, Bundle settings) {
      mUserManager.setApplicationRestrictions(packageName, settings, userHandle);       
}
//frameworks/base/core/java/android/os/UserManager.java
@WorkerThread
public void setApplicationRestrictions(String packageName, Bundle restrictions,
            UserHandle user) {
      mService.setApplicationRestrictions(packageName, restrictions, user.getIdentifier());
}
//frameworks/base/services/core/java/com/android/server/pm/UserManagerService.java
@Override
public void setApplicationRestrictions(String packageName, Bundle restrictions,
            int userId) {
      checkSystemOrRoot("set application restrictions");
      if (restrictions != null) {
          restrictions.setDefusable(true);
      }
      synchronized (mAppRestrictionsLock) {
          //file
          ///data/system/users/userId/res_'packagename'.xml
          if (restrictions == null || restrictions.isEmpty()) {
              //Removes the app restrictions file for a specific package and user id, if it exists.
              cleanAppRestrictionsForPackageLAr(packageName, userId);
          } else {
              // Write the restrictions to XML
              writeApplicationRestrictionsLAr(packageName, restrictions, userId);
          }
      }

      // Notify package of changes via an intent - only sent to explicitly registered receivers.
      Intent changeIntent = new Intent(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
      changeIntent.setPackage(packageName);
      changeIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
      mContext.sendBroadcastAsUser(changeIntent, UserHandle.of(userId));
}

```

## setApplicationRestrictionsManagingPackage

```java
//设置代理应用可以管理ApplicationRestrictions
@TargetApi(Build.VERSION_CODES.N)
    private void setApplicationRestrictionsManagingPackage(String pkgName) {
            mDpm.setApplicationRestrictionsManagingPackage(
                DeviceOwnerReceiver.getComponentName(getActivity()), pkgName);
       
    }
//使用代理应用设置ApplicationRestrictions
DevicePolicyManager manager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        final Bundle bundle = new Bundle();
        bundle.putString("test", "value");
        manager.setApplicationRestrictions(
                null, "要限制的包名",
                bundle);
```

# getApplicationRestrictions

获取DevicePolicyManager.setApplicationRestrictions设置的限制

## 1、RestrictionsManager

```java
//frameworks/base/core/java/android/content/RestrictionsManager.java
public Bundle getApplicationRestrictions() {
     return mService.getApplicationRestrictions(mContext.getPackageName());   
}
//frameworks/base/ervices/restrictions/java/com/android/server/restrictions/RestrictionsManagerService.java
@Override
public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
     return mUm.getApplicationRestrictions(packageName);
}
```

## 2、UserManager

```java
//frameworks/base/core/java/android/os/UserManager.java
@WorkerThread
public Bundle getApplicationRestrictions(String packageName) {
      return mService.getApplicationRestrictions(packageName);
}
@WorkerThread
public Bundle getApplicationRestrictions(String packageName, UserHandle user) {
      return mService.getApplicationRestrictionsForUser(packageName, user.getIdentifier());
}
//frameworks/base/services/core/java/com/android/server/pm/UserManagerService.java
@Override
public Bundle getApplicationRestrictions(String packageName) {
      return getApplicationRestrictionsForUser(packageName, UserHandle.getCallingUserId());
}
@Override
public Bundle getApplicationRestrictionsForUser(String packageName, int userId) {
      if (UserHandle.getCallingUserId() != userId
              || !UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(packageName))) {
          checkSystemOrRoot("get application restrictions for other user/app " + packageName);
      }
      synchronized (mAppRestrictionsLock) {
            // Read the restrictions from XML
          return readApplicationRestrictionsLAr(packageName, userId);
      }
}
```

