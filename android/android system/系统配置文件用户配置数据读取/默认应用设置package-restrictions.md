配置时同时也会更新到/data/system/users/0/roles.xml 文件
Home:
PackageManagerInternal.DefaultHomeProvider 通知provider更新


## 配置文件

```xml
/data/system/users/0/package-restrictions.xml
 <preferred-activities>
        <item name="com.android.dialer/.main.impl.MainActivity" match="200000" always="true" set="1">
            <set name="com.android.dialer/.main.impl.MainActivity" />
            <filter>
                <action name="android.intent.action.DIAL" />
                <cat name="android.intent.category.DEFAULT" />
                <scheme name="tel" />
            </filter>
        </item>
        <item name="com.android.launcher3/.Launcher" match="100000" always="false" set="0">
            <filter>
                <action name="android.intent.action.MAIN" />
                <cat name="android.intent.category.HOME" />
                <cat name="android.intent.category.DEFAULT" />
            </filter>
        </item>
        <item name="com.android.dialer/.main.impl.MainActivity" match="100000" always="true" set="1">
            <set name="com.android.dialer/.main.impl.MainActivity" />
            <filter>
                <action name="android.intent.action.DIAL" />
                <cat name="android.intent.category.DEFAULT" />
            </filter>
        </item>
    </preferred-activities>
```

```
//添加
com.android.server.pm.PackageManagerService#addPreferredActivity
//在启动应用时匹配到多个应用时 会查找Preferred
com.android.server.pm.PackageManagerService#findPreferredActivityNotLocked
```

```
保存配置文件
com.android.server.pm.Settings#writePackageRestrictionsLPr
```

```
//添加PersistentPreferred 优先级高于Preferred
android.app.admin.DevicePolicyManager#addPersistentPreferredActivity
//在启动应用时匹配到多个应用时 会查找PersistentPreferred
com.android.server.pm.PackageManagerService#findPersistentPreferredActivityLP
```

