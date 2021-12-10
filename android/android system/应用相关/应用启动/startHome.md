# 启动home应用

```java
com.android.server.am.ActivityManagerService#systemReady{
	mAtmInternal.startHomeOnAllDisplays(currentUserId, "systemReady");
}
com.android.server.wm.ActivityTaskManagerService.LocalService#startHomeOnAllDisplays{
    mRootActivityContainer.startHomeOnAllDisplays(userId, reason);
}
com.android.server.wm.RootActivityContainer#startHomeOnAllDisplays{
    com.android.server.wm.RootActivityContainer#startHomeOnDisplay()
}

boolean startHomeOnDisplay(int userId, String reason, int displayId, boolean allowInstrumenting, boolean fromHomeKey) {
    // Fallback to top focused display if the displayId is invalid.
    if (displayId == INVALID_DISPLAY) {
        displayId = getTopDisplayFocusedStack().mDisplayId;
    }

    Intent homeIntent = null;
    ActivityInfo aInfo = null;
    if (displayId == DEFAULT_DISPLAY) {
        homeIntent = mService.getHomeIntent();
        aInfo = resolveHomeActivity(userId, homeIntent);
    } else if (shouldPlaceSecondaryHomeOnDisplay(displayId)) {
        Pair<ActivityInfo, Intent> info = resolveSecondaryHomeActivity(userId, displayId);
        aInfo = info.first;
        homeIntent = info.second;
    }
    if (aInfo == null || homeIntent == null) {
        return false;
    }

    if (!canStartHomeOnDisplay(aInfo, displayId, allowInstrumenting)) {
        return false;
    }

    // Updates the home component of the intent.
    homeIntent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
    homeIntent.setFlags(homeIntent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
    // Updates the extra information of the intent.
    if (fromHomeKey) {
        homeIntent.putExtra(WindowManagerPolicy.EXTRA_FROM_HOME_KEY, true);
    }
    // Update the reason for ANR debugging to verify if the user activity is the one that
    // actually launched.
    final String myReason = reason + ":" + userId + ":" + UserHandle.getUserId(
        aInfo.applicationInfo.uid) + ":" + displayId;
    mService.getActivityStartController().startHomeActivity(homeIntent, aInfo, myReason,
                                                            displayId);
    return true;
}
```

```
Provision也会响应android.intent.category.HOME属性，并且它的android:priority属性值为3，整数值越大，优先级越高，可见它的优先级要比一般的Launcher高，所以在AMS的startHomeActivityLocked()方法启动HomeLauncher的时候会先启动Provision应用，即开机向导，然后才启动Launcher。

```

