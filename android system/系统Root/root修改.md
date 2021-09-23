# android 10

```
	modified:    build/make/core/main.mk
	modified:    system/core/adb/Android.bp
	modified:    system/core/adb/daemon/main.cpp
	modified:    system/core/fs_mgr/Android.bp
	modified:    system/core/init/selinux.cpp
	modified:    system/sepolicy/Android.mk
	modified:    system/sepolicy/definitions.mk

```

## 1、让进程名称在 AS LOGCAT 中可见，通过修改 RO.ADB.SECURE 和 RO.SECURE

**build/make/core/main.mk**

```makefile
 tags_to_install :=
 ifneq (,$(user_variant))
   # Target is secure in user builds.
-  ADDITIONAL_DEFAULT_PROPERTIES += ro.secure=1
+  # ADDITIONAL_DEFAULT_PROPERTIES += ro.secure=1
+  ADDITIONAL_DEFAULT_PROPERTIES += ro.secure=0
   ADDITIONAL_DEFAULT_PROPERTIES += security.perf_harden=1
 
   ifeq ($(user_variant),user)
-    ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=1
+    # ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=1
+    ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=0
   endif
 
   ifeq ($(user_variant),userdebug)
@@ -251,7 +253,7 @@ ifneq (,$(user_variant))
     tags_to_install += debug
   else
     # Disable debugging in plain user builds.
-    enable_target_debugging :=
+    # enable_target_debugging :=
   endif
 
   # Disallow mock locations by default for user builds


```

## 2、修改 SELINUX权限为 PERMISSIVE

SELinux 常用状态有两个 Permissive 和 Enforcing，通过 adb shell getenforce 可查看当前所处模式10.0 改到了 selinux.cpp 中

**system/core/init/selinux.cpp**

```cpp
 bool IsEnforcing() {
+    return false;
     if (ALLOW_PERMISSIVE_SELINUX) {
         return StatusFromCmdline() == SELINUX_ENFORCING;
     }
```

## 3、修改 SEPOLICY 编译规则为 ENG

**system/sepolicy/Android.mk**

```makefile
+++ b/system/sepolicy/Android.mk
@@ -309,7 +309,7 @@ LOCAL_REQUIRED_MODULES += \
 
 endif
 
-ifneq ($(TARGET_BUILD_VARIANT), user)
+ifneq ($(TARGET_BUILD_VARIANT), eng)
 LOCAL_REQUIRED_MODULES += \
     selinux_denial_metadata \
 
@@ -1104,7 +1104,7 @@ endif
 ifneq ($(filter address,$(SANITIZE_TARGET)),)
   local_fc_files += $(wildcard $(addsuffix /file_contexts_asan, $(PLAT_PRIVATE_POLICY)))
 endif
-ifneq (,$(filter userdebug eng,$(TARGET_BUILD_VARIANT)))
+ifneq (,$(filter user userdebug eng,$(TARGET_BUILD_VARIANT)))
   local_fc_files += $(wildcard $(addsuffix /file_contexts_overlayfs, $(PLAT_PRIVATE_POLICY)))
 endif
 ifeq ($(TARGET_FLATTEN_APEX),true)
@@ -1166,7 +1166,7 @@ file_contexts.device.tmp :=
 file_contexts.local.tmp :=
 
 ##################################
-ifneq ($(TARGET_BUILD_VARIANT), user)
+ifneq ($(TARGET_BUILD_VARIANT), eng)
 include $(CLEAR_VARS)
 
 LOCAL_MODULE := selinux_denial_metadata
```

**system/sepolicy/definitions.mk**

```makefile
+++ b/alps/system/sepolicy/definitions.mk
@@ -1,10 +1,11 @@
 # Command to turn collection of policy files into a policy.conf file to be
 # processed by checkpolicy
 define transform-policy-to-conf
 @mkdir -p $(dir $@)
 $(hide) m4 --fatal-warnings $(PRIVATE_ADDITIONAL_M4DEFS) \
        -D mls_num_sens=$(PRIVATE_MLS_SENS) -D mls_num_cats=$(PRIVATE_MLS_CATS) \
-       -D target_build_variant=$(PRIVATE_TARGET_BUILD_VARIANT) \
+       -D target_build_variant=eng \
        -D target_with_dexpreopt=$(WITH_DEXPREOPT) \
        -D target_arch=$(PRIVATE_TGT_ARCH) \

```

## 4、修改 ADB ROOT/REMOUNT 权限, 走 FS_MGR

**system/core/adb/Android.bp**

```
+++ b/system/core/adb/Android.bp
@@ -76,7 +76,15 @@ cc_defaults {
     name: "adbd_defaults",
     defaults: ["adb_defaults"],
 
-    cflags: ["-UADB_HOST", "-DADB_HOST=0"],
+    //cflags: ["-UADB_HOST", "-DADB_HOST=0"],
+    cflags: [
+        "-UADB_HOST",
+        "-DADB_HOST=0",
+        "-UALLOW_ADBD_ROOT",
+        "-DALLOW_ADBD_ROOT=1",
+        "-DALLOW_ADBD_DISABLE_VERITY",
+        "-DALLOW_ADBD_NO_AUTH",
+    ],
     product_variables: {
         debuggable: {
             cflags: [
@@ -403,7 +411,7 @@ cc_library {
         "libcutils",
         "liblog",
     ],
-
+    required: [ "remount",],
     product_variables: {
         debuggable: {
             required: [
```

**system/core/adb/daemon/main.cpp**

```cpp
@@ -63,12 +63,13 @@ static inline bool is_device_unlocked() {
 }
 
 static bool should_drop_capabilities_bounding_set() {
-    if (ALLOW_ADBD_ROOT || is_device_unlocked()) {
+    /*if (ALLOW_ADBD_ROOT || is_device_unlocked()) {
         if (__android_log_is_debuggable()) {
             return false;
         }
     }
-    return true;
+    return true;*/
+    return false;
 }
 
 static bool should_drop_privileges() {


```

**system/core/fs_mgr/Android.bp**

```
+++ b/alps/system/core/fs_mgr/Android.bp
@@ -76,7 +76,8 @@ cc_library {
         "libfstab",
     ],
     cppflags: [
-        "-DALLOW_ADBD_DISABLE_VERITY=0",
+        "-UALLOW_ADBD_DISABLE_VERITY",
+        "-DALLOW_ADBD_DISABLE_VERITY=1",
     ],
     product_variables: {
         debuggable: {
@@ -133,7 +134,8 @@ cc_binary {
         "fs_mgr_remount.cpp",
     ],
     cppflags: [
-        "-DALLOW_ADBD_DISABLE_VERITY=0",
+        "-UALLOW_ADBD_DISABLE_VERITY",
+        "-DALLOW_ADBD_DISABLE_VERITY=1",
     ],
     product_variables: {
         debuggable: {

```







```
 ro.boot.verifiedbootstate
 
 oem lock 相关
 
解锁成功后检查这两个属性会从
[ro.boot.flash.locked]: [1]
[ro.boot.verifiedbootstate]: [green]
变成
[ro.boot.flash.locked]: [0]
[ro.boot.verifiedbootstate]: [orange]
 
```

