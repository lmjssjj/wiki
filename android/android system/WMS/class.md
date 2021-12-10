### IwindowSession

```
// IwindowSession是客户端向wMS请求窗口操作的中间代理，并且是进程唯一的
// 客户端可以通过IWindowSession向 WMS发送请求。
// IWindowSession定义在IWindowSession.aidl文件中
// 是ViewRootImpl和WMS进行通信的代理
// 它是一个Binder对象，真正的实现类是 Session
// /frameworks/base/services/core/java/com/android/server/wm/Session.java
```

#### ViewRootImpl.java

```java
public ViewRootImpl(Context context, Display display) {
      IwindowSession mWindowSession = WindowManagerGlobal.getWindowSession();
}
```

#### WindowManagerGlobal.java

```java
IWindowManager windowManager = getWindowManagerService();
sWindowSession = windowManager.openSession();
```

#### WindowManagerService.java

```java
@Override
public IWindowSession openSession(IWindowSessionCallback callback) {
      return new Session(this, callback);
}
```

### Window

```
	顶层窗口外观和行为策略的抽象基类。这个类的实例应该用作添加到窗口管理器的顶级视图。它提供了标准的UI策略，如背景、标题区域、默认键处理等。
这个抽象类的唯一现有实现是android.view.PhoneWindow，当你需要一个Window时，你应该实例化它。
```

```
Android将窗口大致分为三类：
应用窗口、
子窗口、
系统窗口。
其中，Activity与Dialog属于应用窗口、PopupWindow属于子窗口，必须依附到其他非子窗口才能存在，而Toast属于系统窗口，Dialog可能比较特殊，从表现上来说偏向于子窗口，必须依附Activity才能存在，但是从性质上来说，仍然是应用窗口，有自己的WindowToken
```

```
应用程序窗口：应用程序窗口一般位于最底层，Z-Order在1-99
子窗口：子窗口一般是显示在应用窗口之上，Z-Order在1000-1999
系统级窗口：系统级窗口一般位于最顶层，不会被其他的window遮住，如Toast，Z-Order在2000-2999。
Z-Order越大，window越靠近用户，也就显示越高，高度高的window会覆盖高度低的window。
window的type属性就是Z-Order的值，我们可以给window的type属性赋值来决定window的高度。系统为我们三类window都预设了静态常量
```

```java
/**
  * Start of window types that represent normal application windows.
  */
android.view.WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW = 1;
/**
  * Window type: a normal application window.  The {@link #token} must be
  * an Activity token identifying who the window belongs to.
  * In multiuser systems shows only on the owning user's window.
  */
android.view.WindowManager.LayoutParams.TYPE_APPLICATION = 2;
/**
  * End of types of application windows.
  */
android.view.WindowManager.LayoutParams.LAST_APPLICATION_WINDOW = 99;


/**
  * Start of types of sub-windows.  The {@link #token} of these windows
  * must be set to the window they are attached to.  These types of
  * windows are kept next to their attached window in Z-order, and their
  * coordinate space is relative to their attached window.
  */
android.view.WindowManager.LayoutParams.FIRST_SUB_WINDOW = 1000;
/**
  * End of types of sub-windows.
  */
android.view.WindowManager.LayoutParams.LAST_SUB_WINDOW = 1999;


 /**
   * Start of system-specific window types.  These are not normally
   * created by applications.
   */
android.view.WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW = 2000;

/**
  * End of types of system windows.
  */
android.view.WindowManager.LayoutParams.LAST_SYSTEM_WINDOW      = 2999;
```

### 