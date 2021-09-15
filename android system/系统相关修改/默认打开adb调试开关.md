```
1、
 ifeq ($(user_variant),user)
    ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=1  ===>改成0
  endif
  
2、
 ifeq (true,$(strip $(enable_target_debugging)))
  # Target is more debuggable and adbd is on by default
  ADDITIONAL_DEFAULT_PROPERTIES += ro.debuggable=1
  # Enable Dalvik lock contention logging.
  ADDITIONAL_BUILD_PROPERTIES += dalvik.vm.lockprof.threshold=500
 else # !enable_target_debugging
  # Target is less debuggable and adbd is off by default
  ADDITIONAL_DEFAULT_PROPERTIES += ro.debuggable=0  ===>改成1
 endif # !enable_target_debugging
```

```cpp
//\system\core\adb\daemon\main.cpp
static inline bool is_device_unlocked() {
    return "orange" == android::base::GetProperty("ro.boot.verifiedbootstate", "");
}

#if defined(ALLOW_ADBD_NO_AUTH)
    // If ro.adb.secure is unset, default to no authentication required.
    auth_required = android::base::GetBoolProperty("ro.adb.secure", false);
#elif defined(__ANDROID__)
    if (is_device_unlocked()) {  // allows no authentication when the device is unlocked.
        auth_required = android::base::GetBoolProperty("ro.adb.secure", false);
    }
#endif
```

