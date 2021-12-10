# getSystemService

```java
//获取系统服务 getSystemService
android.content.Context.getSystemService(String)
```

```java
// /frameworks/base/core/java/android/app/ContextImpl.java
@Override
public Object getSystemService(String name) {
    return SystemServiceRegistry.getSystemService(this, name);
}
```

## SystemServiceRegistry

```java
// /frameworks/base/core/java/android/app/SystemServiceRegistry.java
// Not instantiable.
private SystemServiceRegistry() { }
//初始化系统服务的创建接口
static {
   //CHECKSTYLE:OFF IndentationCheck
   registerService(Context.ACCESSIBILITY_SERVICE, AccessibilityManager.class,
           new CachedServiceFetcher<AccessibilityManager>() {
       @Override
       public AccessibilityManager createService(ContextImpl ctx) {
           return AccessibilityManager.getInstance(ctx);
       }});
    ...
    ...
}
//CachedServiceFetcher为SystemServiceRegistry类的抽象静态内部类，实现ServiceFetcher接口并
//实现其定义方法getService方法。
//SystemServiceRegistry类一般只会导入内存一次，然后就留在内存里了。这样就保证了static代码块只会执行一//次，注册各个服务也只各执行一次，获取服务的ServiceFetcher对象也是一个服务对应一个。
```

### 创建缓存

```java
//SystemServiceRegistry类中的缓存对象并没有设置到SystemServiceRegistry类中
//而是存储到了另一个类ContextImpl的成员变量上了。
//每一个ContextImpl都有自己的一个缓存服务cache mServiceCache
/**
 * Creates an array which is used to cache per-Context service instances.
 */
public static Object[] createServiceCache() {
     return new Object[sServiceCacheSize];
}
//在初始化ContextImpl的一个成员变量时被调用了
public class ContextImpl extends Context {
	@UnsupportedAppUsage
	final Object[] mServiceCache = SystemServiceRegistry.createServiceCache();
}
```

