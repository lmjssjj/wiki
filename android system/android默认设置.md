# Android ϵͳĬ������

## ����Ӧ�õ�һЩĬ������

```
/frameworks/base/packages/SettingsProvider/res/values/defaults.xml
	<bool name="def_bluetooth_on">true</bool>
    <bool name="def_wifi_display_on">false</bool>
    <bool name="def_install_non_market_apps">false</bool>
    <bool name="def_package_verifier_enable">true</bool>
```

## Ĭ������

```
frameworks/base/core/java/android/content/res/Configuration.java
setToDefaults()
```

## **Ĭ������, ֪ͨ, ��������**

```
������system.prop �ֱ�����
ro.config.ringtone=Playa.ogg��Ĭ���������ã��ļ���/system/media/audio/ringtones ��ϲ�����������������123.MP3����ringtones�ļ����У���������Ϊro.config.ringtone=123.mp3��
ro.config.notification_sound=regulus.ogg��Ĭ����ʾ�����ļ���/system/media/audio/notifications �޸ķ���ͬ�ϣ�
ro.config.alarm_alert=Alarm_Beep_03.ogg��Ĭ�����壬�ļ���/system/media/audio/alarms �޸ķ���ͬ�ϣ�

���붨��Ĭ��������С
android.media.AudioSystem.DEFAULT_STREAM_VOLUME
loadVolumeLevels()
```

