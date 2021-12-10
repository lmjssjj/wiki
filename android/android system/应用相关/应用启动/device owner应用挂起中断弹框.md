```
packages/apps/Settings/src/com/android/settings/enterprise/ActionDisabledByAdminDialog.java
packages/apps/Settings/src/com/android/settings/enterprise/ActionDisabledByAdminDialogHelper.java
frameworks/base/services/core/java/com/android/server/wm/ActivityStartInterceptor.java
```

```java
//frameworks/base/services/core/java/com/android/server/wm/ActivityStartInterceptor.java
private boolean interceptSuspendedByAdminPackage() {
        DevicePolicyManagerInternal devicePolicyManager = LocalServices
                .getService(DevicePolicyManagerInternal.class);
        if (devicePolicyManager == null) {
            return false;
        }
        mIntent = devicePolicyManager.createShowAdminSupportIntent(mUserId, true);
        mIntent.putExtra(EXTRA_RESTRICTION, POLICY_SUSPEND_PACKAGES);

        mCallingPid = mRealCallingPid;
        mCallingUid = mRealCallingUid;
        mResolvedType = null;

        final UserInfo parent = mUserManager.getProfileParent(mUserId);
        if (parent != null) {
            mRInfo = mSupervisor.resolveIntent(mIntent, mResolvedType, parent.id, 0,
                    mRealCallingUid);
        } else {
            mRInfo = mSupervisor.resolveIntent(mIntent, mResolvedType, mUserId, 0,
                    mRealCallingUid);
        }
        mAInfo = mSupervisor.resolveActivity(mIntent, mRInfo, mStartFlags, null /*profilerInfo*/);
        return true;
    }
```

