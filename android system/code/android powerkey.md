# 电源键

```
PhoneWindowManager.java	frameworks\base\services\core\java\com\android\server\policy	
PhoneWindowManager是运行于systemserver线程中,在Event事件分发之前处理,比如电源键。Event事件分发后,仅有包含Activity的apk线程才可以处理,如果apk中没有activity但是想处理Event事件怎么办呢?可以在PhoneWindowManager做做文章了
```

## 长按电源键

长按电源键的处理行为配置

```java
private boolean hasLongPressOnPowerBehavior() {
        return getResolvedLongPressOnPowerBehavior() != LONG_PRESS_POWER_NOTHING;
}
public void init(...){
	mLongPressOnPowerBehavior = mContext.getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnPowerBehavior);
}
public void updateSettings() {
	mLongPressOnPowerBehavior = Settings.Global.getInt(resolver,
                    Settings.Global.POWER_BUTTON_LONG_PRESS,
                    mContext.getResources().getInteger(
                        com.android.internal.R.integer.config_longPressOnPowerBehavior));
}
//获取长按电源键的行为处理
private int getResolvedLongPressOnPowerBehavior() {
        if (FactoryTest.isLongPressOnPowerOffEnabled()) {
            return LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM;
        }
        return mLongPressOnPowerBehavior;
}
```

```xml
config.xml	frameworks\base\core\res\res\values
<!-- Control the behavior when the user long presses the power button.
            0 - Nothing
            1 - Global actions menu
            2 - Power off (with confirmation)
            3 - Power off (without confirmation)
            4 - Go to voice assist
            5 - Go to assistant (Settings.Secure.ASSISTANT)
  -->
 <integer name="config_longPressOnPowerBehavior">1</integer>
```

处理操作

```java
private void powerLongPress() {...}
```

## 长长按电源键

```java
private boolean hasVeryLongPressOnPowerBehavior() {
        return mVeryLongPressOnPowerBehavior != VERY_LONG_PRESS_POWER_NOTHING;
}

public void init(...){
	mVeryLongPressOnPowerBehavior = mContext.getResources().getInteger(
                com.android.internal.R.integer.config_veryLongPressOnPowerBehavior);
}
public void updateSettings() {
    mVeryLongPressOnPowerBehavior = Settings.Global.getInt(resolver,
                    Settings.Global.POWER_BUTTON_VERY_LONG_PRESS,
                    mContext.getResources().getInteger(
				com.android.internal.R.integer.config_veryLongPressOnPowerBehavior));
}
```



```xml
config.xml	frameworks\base\core\res\res\values
<!-- Control the behavior when the user long presses the power button for a long time.
            0 - Nothing
            1 - Global actions menu
-->
<integer name="config_veryLongPressOnPowerBehavior">0</integer>
```



```java
private void powerVeryLongPress() {...}
```

