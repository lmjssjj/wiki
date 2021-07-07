https://www.cnblogs.com/mingfeng002/p/10948732.html

## Activity window

```java
//Activity.setConetView();
public void setContentView(@LayoutRes int layoutResID) {
    getWindow().setContentView(layoutResID);
    initWindowDecorActionBar();
}
public Window getWindow() {
    return mWindow;
}
//window 的创建
@UnsupportedAppUsage
final void attach() {
     mWindow = new PhoneWindow(this, window, activityConfigCallback);
}
```

```java
//PhoneWindow
//判断mContentParent是否初始化如果没有初始化调用installDecor 。然后将view添加到mContentParent的ViewGroup中

@Override
    public void setContentView(int layoutResID) {
        if (mContentParent == null) {
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }

        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);
        } else {
            mLayoutInflater.inflate(layoutResID, mContentParent);
        }
    }

//installDecor 主要干两件事情:
//如果mDecor没有初始化generateDecor()初始化
//如果mContentParent 没有初始化generateLayout()初始化
//DecorView是整个ViewTree的最顶层View，它是一个FrameLayout布局，代表了整个应用的界面。在该布局下面，有标题view和内容view这两个子元素，而内容view则是上面提到的mContentParent

//generateLayout()给activity的根布设置各种属性

```

​		//DecorView是顶级View，内部有titlebar和contentParent两个子元素，contentParent的id是content，而我们设置的main.xml布局则是contentParent里面的一个子元素。



### DecorView添加至Window

​		每一个Activity组件都有一个关联的Window对象，用来描述一个应用程序窗口。每一个应用程序窗口内部又包含有一个View对象，用来描述应用程序窗口的视图。

​		把DecorView添加到Window对象中。而要了解这个过程，我们首先要简单先了解一下Activity的创建过程： 
​		首先，在ActivityThread#handleLaunchActivity中启动Activity，在这里面会调用到Activity#onCreate方法，从而完成上面所述的DecorView创建动作，当onCreate()方法执行完毕，在handleLaunchActivity方法会继续调用到ActivityThread#handleResumeActivity方法，我们看看这个方法的源码：

```java
//ActivityThread
@Override
public void handleResumeActivity() {
    ViewManager wm = a.getWindowManager();
    wm.addView(decor, l);
}
```

 		**WindowManager\*的\*addView**的过程，**WindowManager**是个接口，它的实现类是**WindowManagerImpl**类，而WindowManagerImpl又把相关逻辑交给了WindowManagerGlobal处理。**WindowManagerGlobal**是个单例类，它在进程中只存在一个实例，是它内部的addView方法最终创建了我们的核心类**ViewRootImpl**。

​		可以看到这里的WindowManagerImpl 的主要功能都是通过WindowManagerGlobal来实现的



​		每个应用进程，仅有一个 sWindowSession 对象，它对应了 WmS 中的 Session 子类，WmS 为每一个应用进程分配一个 Session 对象。WindowState 类有一个 IWindow mClient 参数，是由 Session 调用 addToDisplay 传递过来的，对应了 ViewRootImpl 中的 W 类的实例。



![](1.png)



![](2.png)

- WindowManagerImpl除了保存了窗口所属的屏幕以及父窗口以外，没有任何实质性的工作。窗口的管理都交由WindowManagerGlobal的实例完成。
- WindowManagerGlobal在一个进程中只有一个实例。
- WindowManagerGlobal在3个数组中统一管理整个进程中的所有窗口的信息。这些信息包括控件、布局参数以及ViewRootImpl三个元素。
- 除了管理窗口的上述3个元素以外，WindowManagerGlobal将窗口的创建、销毁与布局更新等任务交付给了ViewRootImpl完成。



![](3.png)





​		WindowManagerService只负责窗口管理，并不负责View的绘制跟图层混合

​		在Android系统中，PopupWindow、Dialog、Activity、Toast等都有窗口的概念，但又各有不同，Android将窗口大致分为三类：应用窗口、子窗口、系统窗口。其中，Activity与Dialog属于应用窗口、PopupWindow属于子窗口，必须依附到其他非子窗口才能存在，而Toast属于系统窗口，Dialog可能比较特殊，从表现上来说偏向于子窗口，必须依附Activity才能存在，但是从性质上来说，仍然是应用窗口，有自己的WindowToken



![](4.png)



# 窗口的添加

```java
TextView mview=new TextView(context);
    ...//设置颜色 样式
    //关键点1
    WindowManager mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    //关键点2
    wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
    wmParams.format = PixelFormat.RGBA_8888;
    wmParams.width = 800;
    wmParams.height = 800;
    //关键点3
    mWindowManager.addView(mview, wmParams);

//关键点1：获取WindowManagerService服务的代理对象，不过对于Application而言，获取到的其实是一个封装过的代理对象，一个WindowManagerImpl实例，Application 的getSystemService(）源码其实是在ContextImpl中

//因此context.getApplicationContext().getSystemService()最终可以简化为new WindowManagerImpl(ctx.getDisplay())，下面看下WindowManagerImpl的构造方法，它有两个实现方法，对于Activity跟Application其实是有区别的

//因此context.getApplicationContext().getSystemService()最终可以简化为new WindowManagerImpl(ctx.getDisplay())，下面看下WindowManagerImpl的构造方法，它有两个实现方法，对于Activity跟Application其实是有区别的

//对于Application采用的是一参构造方法，所以其mParentWindow=null，这点后面会有用，到这里，通过getService获取WMS代理的封装类，接着看第二点，WindowManager.LayoutParams，主要看一个type参数，这个参数决定了窗口的类型，这里我们定义成一个Toast窗口，属于系统窗口，不需要处理父窗口、子窗口之类的事，更容易分析，最后看关键点3，利用WindowManagerImpl的addView方法添加View到WMS

public WindowManagerImpl(Display display) {
    this(display, null);
}

 @Override
public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
    applyDefaultToken(params);
    mGlobal.addView(view, params, mDisplay, mParentWindow);
}

//不过很明显WindowManagerImpl最后是委托mGlobal来进行这项操作，WindowManagerGlobal是一个单利，一个进程只有一个

public void addView(View view, ViewGroup.LayoutParams params,
      
        ViewRootImpl root;
        View panelParentView = null;

        synchronized (mLock) {
           //...
            root = new ViewRootImpl(view.getContext(), display);

            view.setLayoutParams(wparams);

            mViews.add(view);
            mRoots.add(root);
            mParams.add(wparams);
        }

        // do this last because it fires off messages to start doing things
        try {
            root.setView(view, wparams, panelParentView);
        } catch (RuntimeException e) {
           //...
        }
    }

//在向WMS添加View的时候，WindowManagerGlobal首先为View新建了一个ViewRootImpl，ViewRootImpl可以看做也是Window和View之间的通信的纽带，比如将View添加到WMS、处理WMS传入的触摸事件、通知WMS更新窗口大小等、同时ViewRootImpl也封装了View的绘制与更新方法等

public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
        synchronized (this) {
            if (mView == null) {
                mView = view;
                  ...
                    //关键点1 
                // Schedule the first layout -before- adding to the window
                // manager, to make sure we do the relayout before receiving
                // any other events from the system.
                requestLayout();
                if ((mWindowAttributes.inputFeatures
                        & WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL) == 0) {
                    mInputChannel = new InputChannel();
                }
                try {
                    //关键点2
                    res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes,
                            getHostVisibility(), mDisplay.getDisplayId(),
                            mAttachInfo.mContentInsets, mAttachInfo.mStableInsets,
                            mAttachInfo.mOutsets, mInputChannel);
                } catch (RemoteException e) {
               ...
            }
}
//先看关键点1，这里是先为relayout占一个位置，其实是依靠Handler先发送一个Message，排在所有WMS发送过来的消息之前，先布局绘制一次，之后才会处理WMS传来的各种事件，比如触摸事件等，毕竟要首先将各个View的布局、位置处理好，才能准确的处理WMS传来的事件。
//接着看做关键点2，这里才是真正添加窗口的地方，虽然关键点1执行在前，但是用的是Handler发消息的方式来处理，其Runable一定是在关键点2之后执行，接着看关键点2，这里有个比较重要的对象mWindowSession与mWindow

//mWindowSession它是通过WindowManagerGlobal.getWindowSession获得的一个Binder服务代理，是App端向WMS发送消息的通道。
//相对的，mWindow是一个W extends IWindow.Stub Binder服务对象
//mWindowSession其实可以看做是App端的窗口对象，主要作用是传递给WMS，并作为WMS向APP端发送消息的通道，在Android系统中存在大量的这种互为C\S的场景。接着看mWindowSession获取的具体操首先通过getWindowManagerService 获取WMS的代理，之后通过WMS的代理在服务端open一个Session，并在APP端获取该Session的代理

//首先要记住sWindowSession是一个单例的对象，之后就可以将getWindowManagerService函数其实可以简化成下面一句代码，其实就是获得WindowManagerService的代理，之前的WindowManagerImpl都是一个壳子，或者说接口封装
       
//sWindowSession = windowManager.openSession，它通过binder驱动后，会通知WMS回调openSession，打开一个Session返回给APP端，而Session extends IWindowSession.Stub ，很明显也是一个Binder通信的Stub端，封装了每一个Session会话的操作。
            

```



![](5.png)



​		

下面就是利用Session来add一个窗口：其实是调用Session.java的addToDisplayWithoutInputChannel函数

```java
final class Session extends IWindowSession.Stub
        implements IBinder.DeathRecipient {
　　　　　　//...
        @Override
　　　　public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams attrs,
        int viewVisibility, int displayId, Rect outContentInsets, Rect outStableInsets,
        Rect outOutsets, InputChannel outInputChannel) {
         return mService.addWindow(this, window, seq, attrs, viewVisibility, displayId,
               outContentInsets, outStableInsets, outOutsets, outInputChannel);
　　}
}
```

![](6.png)

在WindowManager的LayoutParams中，与type同等重要的还有token。

上面说到：在源码中token一般代表的是Binder对象，作用于IPC进程间数据通讯。并且它也包含着此次通讯所需要的信息，在ViewRootImpl里，token用来表示mWindow(W类，即IWindow)，并且在WmS中只有符合要求的token才能让Window正常显示。

如此一来，Window的添加请求就交给WmS去处理了，在WmS内部会为每一个应用保留一个单独的Session。在WmS 端会创建一个WindowState对象用来表示当前添加的窗口。 WmS负责管理这里些 WindowState 对象。至此，Window的添加过程就结束了。



