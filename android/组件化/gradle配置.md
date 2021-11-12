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



在gradle中为生成的BuildConfig添加属性

```
android {

    buildTypes {
        release {      
            buildConfigField 'boolean','is_application',rootProject.ext.android.is_application.toString()
        }
        advanced{
            buildConfigField 'boolean','is_application',rootProject.ext.android.is_application.toString()
        }
        debug{
            buildConfigField 'boolean','is_application',rootProject.ext.android.is_application.toString()
        }
    }
}

public final class BuildConfig {
    public static final boolean is_application = false;
}
```