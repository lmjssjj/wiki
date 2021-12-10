```
vendor\mediatek\proprietary\frameworks\base\core\java\com\mediatek\amsAal\AalUtils.java
vendor\mediatek\proprietary\frameworks\base\core\java\com\mediatek\amsAal\ams_aal_config.xml
```

```xml
<!--
aal_mode:
    AAL_MODE_PERFORMANCE = 0;
    AAL_MODE_BALANCE = 1;
    AAL_MODE_LOWPOWER = 2;

component:
    Package: com.foo
    Activity: com.foo/.Blah

backlight Level: 0~255 (Default : 128)
-->
<ams_aal>
	<!-- AAL_MODE_PERFORMANCE -->
    <config aal_mode="0" component="com.android.launcher3" backlight="160"/>
    <!-- AAL_MODE_BALANCE -->
    <config aal_mode="1" component="com.android.launcher3" backlight="192"/>
    <!-- AAL_MODE_LOWPOWER -->
    <config aal_mode="2" component="com.android.launcher3" backlight="240"/>
</ams_aal>
```

```java
//设置backlight level
在activity onresume 时 回调
ActivityStack.resumeTopActivityInnerLocked{
	mService.mAmsExt.onAfterActivityResumed(next);
}
ActivityStack.minimalResumeActivityLocked{
        /// M: onAfterActivityResumed @{
        mService.mAmsExt.onAfterActivityResumed(r);
        /// M: onAfterActivityResumed @}
}
ActivityRecord.relaunchActivityLocked{
	mService.mAmsExt.onAfterActivityResumed(r);
}
```

```java
com.mediatek.server.am.AmsExtImpl#onAfterActivityResumed{
	if (mAalUtils != null) {
            mAalUtils.onAfterActivityResumed(packageName, activityName);
        }
}
```

```
com.mediatek.amsAal.AalUtils#setSmartBacklightInternal
```

```
设置aalmode
ActivityManager.setAalMode
ActivityManagerService.setAalMode
com.mediatek.server.am.AmsExtImpl#setAalMode
com.mediatek.amsAal.AalUtils#setAalMode
```

