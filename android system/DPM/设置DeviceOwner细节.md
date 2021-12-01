## 设置命令

```
usage: dpm set-device-owner [ --user <USER_ID> | current *EXPERIMENTAL* ] [ --name <NAME> ] <COMPONENT>
usage: dpm remove-active-admin [ --user <USER_ID> | current ] [ --name <NAME> ] <COMPONENT>
dpm set-device-owner: Sets the given component as active admin, and its package as device owner.
dpm remove-active-admin: Disables an active admin, the admin must have declared android:testOnly in the application in its manifest. This will also remove device and profile owners
```

## 设置条件

- 如果不是 Process.SHELL_UID || Process.ROOT_UID 调用设置device owner时：
  需要验证申请权限android.Manifest.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS
- 设备必须是没有设置deviceowner的
- 设备当前用户必须是没有设置ProfileOwner的
- 设备当前用户必须是正在运行
- 当前设备是手表并且已经配对是不行的
- 设置进程是 Process.SHELL_UID || Process.ROOT_UID 时 并且是手表或者已经设置完成的设备时 设备不能运行分裂系统用户(它的意思是系统用户和主用户是两个独立的用户)
-  设置进程是 Process.SHELL_UID || Process.ROOT_UID 时 并且是手表或者已经设置完成的设备(开机向导完成) 当前用户不能有账户
- 设备不是运行分裂系统用户(它的意思是系统用户和主用户是两个独立的用户)设置deviceowner的用户必须是UserHandle.USER_SYSTEM
- 设备不是运行分裂系统用户(它的意思是系统用户和主用户是两个独立的用户)当前不能是已经设置完成的设备(开机向导完成)

