

```java
//com.android.server.SystemConfig#readPermissionsFromXml
//or
//com.android.server.SystemConfig#SystemConfig
if (SystemProperties.getBoolean("ro.vendor.test", false)) {
    android.util.Log.d("lmjssjj","SystemConfig");
    addFeature("com.test.hardware.display.test", 0);
}
```

