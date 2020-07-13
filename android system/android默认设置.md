# Android 系统默认设置

## 设置应用的一些默认设置

```
/frameworks/base/packages/SettingsProvider/res/values/defaults.xml
	<bool name="def_bluetooth_on">true</bool>
    <bool name="def_wifi_display_on">false</bool>
    <bool name="def_install_non_market_apps">false</bool>
    <bool name="def_package_verifier_enable">true</bool>
```

## 默认字体

```
frameworks/base/core/java/android/content/res/Configuration.java
setToDefaults()
```

## **默认铃声, 通知, 闹钟音乐**

```
可以在system.prop 分别配置
ro.config.ringtone=Playa.ogg（默认铃声设置，文件在/system/media/audio/ringtones 把喜欢的铃声放这里，比如123.MP3放入ringtones文件夹中，这里代码改为ro.config.ringtone=123.mp3）
ro.config.notification_sound=regulus.ogg（默认提示音，文件在/system/media/audio/notifications 修改方法同上）
ro.config.alarm_alert=Alarm_Beep_03.ogg（默认闹铃，文件在/system/media/audio/alarms 修改方法同上）

代码定义默认铃声大小
android.media.AudioSystem.DEFAULT_STREAM_VOLUME
loadVolumeLevels()
```

