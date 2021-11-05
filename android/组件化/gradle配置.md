组件化主要依赖于对gradle的配置来动态修改module是lib还是app类型

# 1、定义全局项目配置

```groovy
//config.gradle
ext {  //extend
    // false: 组件模式
    // true ：集成模式
    isModule = true
    android = [
            compileSdkVersion: 28,
            minSdkVersion    : 15,
            targetSdkVersion : 28,
            versionCode      : 1,
            versionName      : "1.0"
    ]

    appId = ["app"  : "主模块ID",
             "module1": "module1",
             "module2" : "module2" ]

    supportLibrary = "28.0.0"
    dependencies = [
            "appcompat-v7"     : "com.android.support:appcompat-v7:${supportLibrary}",
    ]
}
```

