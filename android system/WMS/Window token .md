https://blog.csdn.net/weixin_43766753/article/details/109060496

```
android.view.WindowManager.LayoutParams.token
```

```java
android.view.WindowManagerGlobal.addView(View, LayoutParams, Display, Window)
public void addView() {
	if (parentWindow != null) {
            parentWindow.adjustLayoutParamsForSubWindow(wparams);
     }
}
```

```java
Window.class
void adjustLayoutParamsForSubWindow(WindowManager.LayoutParams wp) {
	if (wp.type >= WindowManager.LayoutParams.FIRST_SUB_WINDOW &&
                wp.type <= WindowManager.LayoutParams.LAST_SUB_WINDOW) {
            if (wp.token == null) {
                View decor = peekDecorView();
                if (decor != null) {
                    wp.token = decor.getWindowToken();
                }
            }
           else if (wp.type >= WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW &&
                wp.type <= WindowManager.LayoutParams.LAST_SYSTEM_WINDOW) {
        } else {
            if (wp.token == null) {
                wp.token = mContainer == null ? mAppToken : mContainer.mAppToken;
            }
        }
}
```

​		android.view.WindowManager.LayoutParams.token 是来自Window的mAppToken，或者 父window的mAppToken



​		Window 的token 来自：

```java
Window.class
public void setWindowManager(WindowManager wm, IBinder appToken, String appName,
            boolean hardwareAccelerated) {
        mAppToken = appToken;
}
```

```java
Activity.class
android.app.Activity.attach()
@UnsupportedAppUsage
final void attach(IBinder token) {
        mToken = token;
        mWindow.setWindowManager(
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
                mToken, mComponent.flattenToString(),
                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
}
```

```java
ActivityThread.class
android.app.ActivityThread.performLaunchActivity(ActivityClientRecord, Intent)
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
  
          
       activity.attach(appContext, this, getInstrumentation(), r.token,
                r.ident, app, r.intent, r.activityInfo, title, r.parent,
                r.embeddedID, r.lastNonConfigurationInstances, config,
                r.referrer, r.voiceInteractor, window, r.configCallback,
                r.assistToken);
        return activity;
    }
```

```java
ActivityThread.class
@Override
public Activity handleLaunchActivity(ActivityClientRecord r,
            PendingTransactionActions pendingActions, Intent customIntent) {

        final Activity a = performLaunchActivity(r, customIntent);
        return a;
}
```

```java
LaunchActivityItem.class
android.app.servertransaction.LaunchActivityItem.execute(ClientTransactionHandler, IBinder, PendingTransactionActions)
@Override
public void execute(ClientTransactionHandler client, IBinder token,
            PendingTransactionActions pendingActions) {
        ActivityClientRecord r = new ActivityClientRecord(token, mIntent, mIdent, mInfo,
                mOverrideConfig, mCompatInfo, mReferrer, mVoiceInteractor, mState, mPersistentState,
                mPendingResults, mPendingNewIntents, mIsForward,
                mProfilerInfo, client, mAssistToken);
        client.handleLaunchActivity(r, pendingActions, null /* customIntent */);
}
```

```java
TransactionExecutor.class
android.app.servertransaction.TransactionExecutor.executeCallbacks(ClientTransaction)
@VisibleForTesting
public void executeCallbacks(ClientTransaction transaction) {
        final IBinder token = transaction.getActivityToken();
        ActivityClientRecord r = mTransactionHandler.getActivityClient(token);
}
```

```java
TransactionExecutor.class
android.app.servertransaction.TransactionExecutor.execute(ClientTransaction)
public void execute(ClientTransaction transaction) {
        executeCallbacks(transaction);
}
```

```java
android.app.ActivityThread.H.handleMessage(Message)
case EXECUTE_TRANSACTION:
           final ClientTransaction transaction = (ClientTransaction) msg.obj;
           mTransactionExecutor.execute(transaction);
```

​	

​	ClientTransaction是AMS传来的一个事务，负责控制activity的启动，里面包含两个item一个负责执行activity的create工作，一个负责activity的resume工作。

```java
ActivityStackSupervisor.class
com.android.server.wm.ActivityStackSupervisor.realStartActivityLocked(ActivityRecord, WindowProcessController, boolean, boolean)
boolean realStartActivityLocked(ActivityRecord r, WindowProcessController proc,
            boolean andResume, boolean checkConfig) throws RemoteException {
     // Create activity launch transaction.
     final ClientTransaction clientTransaction = ClientTransaction.obtain(
           proc.getThread(), r.appToken);
}
```

```java
ActivityStarter.class
com.android.server.wm.ActivityStarter.startActivity(IApplicationThread, Intent, Intent, String, ActivityInfo, ResolveInfo, IVoiceInteractionSession, IVoiceInteractor, IBinder, String, int, int, int, String, int, int, int, SafeActivityOptions, boolean, boolean, ActivityRecord[], TaskRecord, boolean, PendingIntentRecord, boolean)
private int startActivity() {
	ActivityRecord r = new ActivityRecord(mService, callerApp, callingPid, callingUid,
                callingPackage, intent, resolvedType, aInfo, mService.getGlobalConfiguration(),
                resultRecord, resultWho, requestCode, componentSpecified, voiceSession != null,
                mSupervisor, checkedOptions, sourceRecord);
}
```

```java
ActivityRecord.class
com.android.server.wm.ActivityRecord.ActivityRecord(ActivityTaskManagerService, WindowProcessController, int, int, String, Intent, String, ActivityInfo, Configuration, ActivityRecord, String, int, boolean, boolean, ActivityStackSupervisor, ActivityOptions, ActivityRecord)
ActivityRecord() {
        appToken = new Token(this, _intent);
}
```

```java
static class Token extends IApplicationToken.Stub {}
```

​		可以看到确实这里进行了token创建。而这个token看接口就知道是个Binder对象，他持有ActivityRecord的弱引用，这样可以访问到activity的所有信息。到这里token的创建我们也找到了。那么WMS是怎么知道一个token是否合法呢？每个token创建后，会在后续发送到WMS ，WMS对token进行缓存，而后续对于应用发送来的token只需要在缓存拿出来匹配一下就知道是否合法了。那么WMS是怎么拿到token的？

```java
ActivityStack.class
com.android.server.wm.ActivityStack.startActivityLocked(ActivityRecord, ActivityRecord, boolean, boolean, ActivityOptions)
void startActivityLocked(ActivityRecord r, ActivityRecord focusedTopActivity,
            boolean newTask, boolean keepCurTransition, ActivityOptions options) {
            r.createAppWindowToken();
}
```

```java
com.android.server.wm.ActivityRecord.createAppWindowToken()
void createAppWindowToken() {
        if (mAppWindowToken != null) {
            throw new IllegalArgumentException("App Window Token=" + mAppWindowToken
                    + " already created for r=" + this);
        }
        mAppWindowToken = mAtmService.mWindowManager.mRoot.getAppWindowToken(appToken.asBinder());
        if (mAppWindowToken != null) {
        } else {
            mAppWindowToken = createAppWindow(mAtmService.mWindowManager, appToken,
                    task.voiceSession != null, container.getDisplayContent(),
                    ActivityTaskManagerService.getInputDispatchingTimeoutLocked(this)
                            * 1000000L, fullscreen,
                    (info.flags & FLAG_SHOW_FOR_ALL_USERS) != 0, appInfo.targetSdkVersion,
                    info.screenOrientation, mRotationAnimationHint,
                    mLaunchTaskBehind, isAlwaysFocusable());
        }

        task.addActivityToTop(this);

    }
```

```java
WindowToken.class
com.android.server.wm.WindowToken.WindowToken()
WindowToken() {
        onDisplayChanged(dc);
    }
```

```java
WindowToken.class
@Override
void onDisplayChanged(DisplayContent dc) {
        dc.reParentWindowToken(this);
}
```

```java
com.android.server.wm.DisplayContent.reParentWindowToken(WindowToken)
/** Changes the display the input window token is housed on to this one. */
void reParentWindowToken(WindowToken token) {
        addWindowToken(token.token, token);
}
```

```java
DisplayContent
private void addWindowToken(IBinder binder, WindowToken token) {
        final DisplayContent dc = mWmService.mRoot.getWindowTokenDisplay(token);
        mTokenMap.put(binder, token);
}
```

![](image\7.png)
