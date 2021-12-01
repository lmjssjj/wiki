# PermissionManagerService

## PermissionSettings

```java

//com.android.server.pm.PackageManagerService#PackageManagerService
//在PackageManagerService构造方法中调用了
PackageManagerService(){}
	//在create中实例化了PermissionManagerService
 	mPermissionManager = PermissionManagerService.create();
	//settings 中读取 runtime-permissions.xml
	mFirstBoot = !mSettings.readLPw(sUserManager.getUsers(false));
}
                                                         
//com.android.server.pm.permission.PermissionManagerService#PermissionManagerService
//在PermissionManagerService构造方法中调用了
PermissionManagerService(){
    //创建了PermissionSettings存放权限信息
    mSettings = new PermissionSettings(mLock);
    //创建了DefaultPermissionGrantPolicy默认权限初始化类
    mDefaultPermissionGrantPolicy = new DefaultPermissionGrantPolicy(
                context, mHandlerThread.getLooper(), this);
    //获取SystemConfig读取的权限并初始化到PermissionSettings中
    SystemConfig systemConfig = SystemConfig.getInstance();
    mSystemPermissions = systemConfig.getSystemPermissions();
}
```

## 读取权限文件 runtime-permissions.xml

### Settings

```java
//Settings.java
private static final String RUNTIME_PERMISSIONS_FILE_NAME = "runtime-permissions.xml";
boolean readLPw(@NonNull List<UserInfo> users) {
    for (UserInfo user : users) {
         mRuntimePermissionsPersistence.readStateForUserSyncLPr(user.id);
    }
}
//通过读取runtime-permissions.xml权限并将权限是否开启赋值给PermissionSettings
BasePermission bp = mPermissions.getPermission(name);
//并保存在
PackageSetting 中的 protected final PermissionsState mPermissionsState;
```

