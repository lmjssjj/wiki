## 1.修改/aosp/system/extras/su/su.cpp

```cpp
// 注释掉第83-84行
// uid_t current_uid = getuid();
// if (current_uid != AID_ROOT && current_uid != AID_SHELL) error(1, 0, "not allowed");
```

## 2.修改/aosp/system/core/libcutils/fs_config.cpp

```cpp
// the following files have enhanced capabilities and ARE included
// in user builds.
// 添加下面代码至212行处，注意标点符号不要漏掉
{ 06755, AID_ROOT,      AID_ROOT,      0, "system/bin/su" },
```

## 3. 修改/aosp/frameworks/base/core/jni/com_android_internal_os_Zygote.cpp

```cpp
// 修改542行处，注释掉DropCapabilitiesBoundingSet方法体
static void DropCapabilitiesBoundingSet(fail_fn_t fail_fn) {
//   for (int i = 0; prctl(PR_CAPBSET_READ, i, 0, 0, 0) >= 0; i++) {;
//     if (prctl(PR_CAPBSET_DROP, i, 0, 0, 0) == -1) {
//       if (errno == EINVAL) {
//         ALOGE("prctl(PR_CAPBSET_DROP) failed with EINVAL. Please verify "
//               "your kernel is compiled with file capabilities support");
//       } else {
//         fail_fn(CREATE_ERROR("prctl(PR_CAPBSET_DROP, %d) failed: %s", i, strerror(errno)));
//       }
//     }
//   }
}
```

## 4. 修改/aosp/system/core/adb/daemon/main.cpp

```cpp
// 修改should_drop_capabilities_bounding_set返回false
static bool should_drop_capabilities_bounding_set() {
    if (ALLOW_ADBD_ROOT || is_device_unlocked()) {
        if (__android_log_is_debuggable()) {
            return false;
        }
    }
//    return true;
    return false;
}
```

## 5. 修改/aosp/system/core/init/selinux.cpp

```cpp
// 修改IsEnforcing方法返回false, 注释掉StatusFromCmdline方法
// EnforcingStatus StatusFromCmdline() {
//     EnforcingStatus status = SELINUX_ENFORCING;

//     import_kernel_cmdline(false,
//                           [&](const std::string& key, const std::string& value, bool in_qemu) {
//                               if (key == "androidboot.selinux" && value == "permissive") {
//                                   status = SELINUX_PERMISSIVE;
//                               }
//                           });

//     return status;
// }

bool IsEnforcing() {
    // if (ALLOW_PERMISSIVE_SELINUX) {
        // return StatusFromCmdline() == SELINUX_ENFORCING;
    // }
    // return true;
    return false;
}
```

