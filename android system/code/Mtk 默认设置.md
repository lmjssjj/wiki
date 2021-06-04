# Settings 默认设置

```
frameworks\base\packages\SettingsProvider中android6.0之后用不在用settings.db这个数据了,会存储在/data/system/users/0这个目录三个xml文件下:

settings_global.xml：所有的偏好设置对系统的所有用户公开，第三方有读没有写的权限
settings_system.xml：包含各种各样的用户偏好系统设置
settings_secure.xml：安全性的用户偏好系统设置，第三方APP有读没有写的权限
在SettingsProvider\src\com\android\providers\settings\SettingsProvider.java中创建
```

## Ota升级某个item

```
强制更新数据
更改SettingsProvider\src\com\android\providers\settings\SettingsProvider.java中的字段SETTINGS_VERSION 加1
```

```java
private final class UpgradeController {
			//ota升级强制更改默认值需要该字段自增一如
        private static final int SETTINGS_VERSION = 132;
}
```

```java
// 在对应的位置强制更新
 private int onUpgradeLocked(int userId, int oldVersion, int newVersion) {
 			if (currentVersion == 157) {
                    // Version 158: Set default value for BACKUP_AGENT_TIMEOUT_PARAMETERS.
                    final SettingsState globalSettings = getGlobalSettingsLocked();
                    final String oldValue = globalSettings.getSettingLocked(
                            Settings.Global.BACKUP_AGENT_TIMEOUT_PARAMETERS).getValue();
                    if (TextUtils.equals(null, oldValue)) {
                        final String defaultValue = getContext().getResources().getString(
                                R.string.def_backup_agent_timeout_parameters);
                        if (!TextUtils.isEmpty(defaultValue)) {
                            globalSettings.insertSettingLocked(
                                    Settings.Global.BACKUP_AGENT_TIMEOUT_PARAMETERS, defaultValue,
                                    null, true,
                                    SettingsState.SYSTEM_PACKAGE_NAME);
                        }
                    }
                    currentVersion = 158;
                }

                if (currentVersion == 158) {
                    // Remove setting that specifies wifi bgscan throttling params
                    getGlobalSettingsLocked().deleteSettingLocked(
                        "wifi_scan_background_throttle_interval_ms");
                    getGlobalSettingsLocked().deleteSettingLocked(
                        "wifi_scan_background_throttle_package_whitelist");
                    currentVersion = 159;
                }
 }
```

## 修改默认中文输入法

```
frameworks/base/core/res/res/values/string.xml中添加:
string name="default_inputmethod">com.iflytek.inputmethod/.FlyIME</string>

/frameworks/base/core/res/res/values/symbols.xml中添加 
<java-symbol type="string" name="default_inputmethod" />

在frameworks/base/packages/SettingsProvider/src/com/android/providers/settings/DatabaseHelper.java中
```

```java
private void loadSecureSettings(SQLiteDatabase db) {
.......
 
            loadStringSetting(stmt, Settings.Secure.ENABLED_INPUT_METHODS,
                      com.android.internal.R.string.default_inputmethod);
 
            loadStringSetting(stmt, Settings.Secure.DEFAULT_INPUT_METHOD,
                      com.android.internal.R.string.default_inputmethod);
......
}
```



# 壁纸

```JAVA
/frameworks/base/core/res/res/drawable-nodpi/default_wallpaper

alps\frameworks\base\core\res\res\values\symbols.xml
  <java-symbol type="drawable" name="default_wallpaper" />
  <java-symbol type="drawable" name="default_lock_wallpaper" />

android.app.WallpaperManager.openDefaultWallpaper(Context, int){
	if (which == FLAG_LOCK) {
            /* Factory-default lock wallpapers are not yet supported
            whichProp = PROP_LOCK_WALLPAPER;
            defaultResId = com.android.internal.R.drawable.default_lock_wallpaper;
            */
            return null;
        } else {
            whichProp = PROP_WALLPAPER;
            defaultResId = com.android.internal.R.drawable.default_wallpaper;
        }
}

```

