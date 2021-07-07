# Application

```java
ActivityThread.class
@UnsupportedAppUsage
private void handleBindApplication(AppBindData data) {
    Application app;
    app = data.info.makeApplication(data.restrictedBackupMode, null);
    
}
```



```java
android.app.LoadedApk.makeApplication(boolean, Instrumentation)
@UnsupportedAppUsage
public Application makeApplication() {
    Application app = null;
    ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
    app = mActivityThread.mInstrumentation.newApplication(
               cl, appClass, appContext);
    
}
```

```java
Instrumentation.class
public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, 
            ClassNotFoundException {
        Application app = getFactory(context.getPackageName())
                .instantiateApplication(cl, className);
        app.attach(context);
        return app;
}
```

```java
Application.class
final void attach(Context context) {
        attachBaseContext(context);
        mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
}
```

```java
ContextImpl.class
@Override
public Object getSystemService(String name) {
        return SystemServiceRegistry.getSystemService(this, name);
}
```

```java
SystemServiceRegistry.class
registerService(Context.WINDOW_SERVICE, WindowManager.class,
                new CachedServiceFetcher<WindowManager>() {
            @Override
            public WindowManager createService(ContextImpl ctx) {
                return new WindowManagerImpl(ctx);
            }});
```



# Activity

```java
ActivityThread.class
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    Activity activity = null;
    java.lang.ClassLoader cl = appContext.getClassLoader();
    activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent);
    activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window, r.configCallback,
                        r.assistToken);
}
```

```java
Activity.class
final void attach() {
    mWindow = new PhoneWindow(this, window, activityConfigCallback);
	mWindow.setWindowManager(
                (WindowManager)context.getSystemService(Context.WINDOW_SERVICE),
                mToken, mComponent.flattenToString(),
                (info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0);
    //获取wm
    mWindowManager = mWindow.getWindowManager();
}
```

```java
Window.class
public void setWindowManager(WindowManager wm, IBinder appToken, String appName,
            boolean hardwareAccelerated) {
        mAppToken = appToken;
        mAppName = appName;
        mHardwareAccelerated = hardwareAccelerated;
        if (wm == null) {
            wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        mWindowManager = ((WindowManagerImpl)wm).createLocalWindowManager(this);
}
```

```java
WindowManagerImpl.class
public WindowManagerImpl createLocalWindowManager(Window parentWindow) {
       return new WindowManagerImpl(mContext, parentWindow);
}
```

