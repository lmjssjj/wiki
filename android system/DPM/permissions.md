## 设置应用权限

```
android.app.admin.DevicePolicyManager#**setPermissionGrantState**
添加PackageManager.FLAG_PERMISSION_POLICY_FIXED标志位
```

```
更新权限到runtime-permissions.xml文件中
com.android.server.pm.Settings#writeRuntimePermissionsForUserLPr
```

