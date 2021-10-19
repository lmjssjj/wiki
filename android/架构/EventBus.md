https://www.jianshu.com/p/d9516884dbd4

# 注册

```java
EventBus.getDefault().register(this);
```

```java
public void register(Object subscriber) {
        // 得到当前要注册类的Class对象
        Class<?> subscriberClass = subscriber.getClass();
        // 根据Class查找当前类中订阅了事件的方法集合，即使用了Subscribe注解、有public修饰符、一个参数的方法
        // SubscriberMethod类主要封装了符合条件方法的相关信息：
        // Method对象、线程模式、事件类型、优先级、是否是粘性事等
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized (this) {
            // 循环遍历订阅了事件的方法集合，以完成注册
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
}
```

```java
List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        // METHOD_CACHE是一个ConcurrentHashMap，直接保存了subscriberClass和对应SubscriberMethod的集合，以提高注册效率，赋值重复查找。
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        // 由于使用了默认的EventBusBuilder，则ignoreGeneratedIndex属性默认为false，即是否忽略注解生成器
        if (ignoreGeneratedIndex) {
            subscriberMethods = findUsingReflection(subscriberClass);
        } else {
            subscriberMethods = findUsingInfo(subscriberClass);
        }
        // 如果对应类中没有符合条件的方法，则抛出异常
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @Subscribe annotation");
        } else {
            // 保存查找到的订阅事件的方法
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
            return subscriberMethods;
        }
    }
```

