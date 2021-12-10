## 同过DPC设置的Apn

```java
public static final Uri DPC_URI = Uri.parse("content://telephony/carriers/dpc");
```

```java
//android.app.admin.DevicePolicyManager#addOverrideApn
com.android.server.devicepolicy.DevicePolicyManagerService#addOverrideApn
{
	resultUri = mContext.getContentResolver().insert(DPC_URI, apnSetting.toContentValues());
}
```

## 设置enable

```java
public static final Uri ENFORCE_MANAGED_URI = Uri.parse(
                "content://telephony/carriers/enforce_managed");
public static final String ENFORCE_KEY = "enforced";
```

```
android.app.admin.DevicePolicyManager#setOverrideApnsEnabled
com.android.server.devicepolicy.DevicePolicyManagerService#setOverrideApnsEnabled
```

