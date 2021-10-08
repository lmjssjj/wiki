# ActivityThread

```
     ApplicationThread是ActivityThread的内部类，也是一个Binder对象。在此处它是作为IApplicationThread对象的server端等待client端的请求然后进行处理，最大的client就是AMS.
     Applicationthread是Activitythread和AMS通信的桥梁(Binder IPC)
```

```java
ActivityThread->attach(){
 	//获取代表类ActivityManagerProxy实例
    final IActivityManager mgr = ActivityManagerNative.getDefault();
    //调用ActivityManagerProxy的attachApplication实施绑定，最终会调用远程类AMS attachApplication完成绑定
     mgr.attachApplication(mAppThread);
}
```

