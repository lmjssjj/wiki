https://www.freesion.com/article/7327221475/

Android 6.0引入的Doze机制在于节省系统耗电量，保护电池，延长电池的使用时间

## Done设备状态

```java
/** Device is currently active.
	设备当前处于活动状态 
*/
@VisibleForTesting
static final int STATE_ACTIVE = 0;
/** Device is inactive (screen off, no motion) and we are waiting to for idle.
	设备处于非活动状态(屏幕关闭，没有动作)，我们正在等待空闲  
*/
@VisibleForTesting
static final int STATE_INACTIVE = 1;
/** Device is past the initial inactive period, and waiting for the next idle period.
	设备已经过了初始的闲置期，正在等待下一个空闲期。  
*/
@VisibleForTesting
static final int STATE_IDLE_PENDING = 2;
/** Device is currently sensing motion. 
	设备目前感应运动。
*/
@VisibleForTesting
static final int STATE_SENSING = 3;
/** Device is currently finding location (and may still be sensing). 
	设备目前正在寻找位置(并且可能仍然是感应的)。  
*/
@VisibleForTesting
static final int STATE_LOCATING = 4;
/** Device is in the idle state, trying to stay asleep as much as possible. 
	设备处于空闲状态，尽量保持休眠状态。  
*/
@VisibleForTesting
static final int STATE_IDLE = 5;
/** Device is in the idle state, but temporarily out of idle to do regular maintenance. 
	设备处于空闲状态，但暂时处于空闲状态，要做定期维护。  
*/
@VisibleForTesting
static final int STATE_IDLE_MAINTENANCE = 6;
/**
* Device is inactive and should go straight into idle (foregoing motion and location
* monitoring), but allow some time for current work to complete first.
  设备处于非活动状态，应该直接进入空闲状态(之前的运动和位置监控)，但允许一些时间先完成当前的工作。
*/
@VisibleForTesting
static final int STATE_QUICK_DOZE_DELAY = 7;
```

```
mLightEnabled = mDeepEnabled = getContext().getResources().getBoolean(
                    com.android.internal.R.bool.config_enableAutoPowerModes);
                    
<!-- Set this to true to enable the platform's auto-power-save modes like doze and
         app standby.  These are not enabled by default because they require a standard
         cloud-to-device messaging service for apps to interact correctly with the modes
         (such as to be able to deliver an instant message to the device even when it is
         dozing).  This should be enabled if you have such services and expect apps to
         correctly use them when installed on your device.  Otherwise, keep this disabled
         so that applications can still use their own mechanisms.
-->
<bool name="config_enableAutoPowerModes">false</bool>
```

## STATE_INACTIVE

进入STATE_INACTIVE条件当前设备没有在充电并且不是强制进入Idle状态，且接收到广播 ACTION_SCREEN_OFF

```java
com.android.server.DeviceIdleController#updateInteractivityLocked{
	becomeInactiveIfAppropriateLocked()
}
com.android.server.DeviceIdleController#becomeInactiveIfAppropriateLocked{
	 if (mLightState == LIGHT_STATE_ACTIVE && mLightEnabled) {
            //设置状态
            mLightState = LIGHT_STATE_INACTIVE;
            //重置监听
            resetLightIdleManagementLocked();
            //设置延时监听
            scheduleLightAlarmLocked(mConstants.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT);
      }
}

//3分钟
LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT = mParser.getDurationMillis(
                        KEY_LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT,
                        !COMPRESS_TIME ? 3 * 60 * 1000L : 15 * 1000L);

//3分钟回调监听
void scheduleLightAlarmLocked(long delay) {
    mNextLightAlarmTime = SystemClock.elapsedRealtime() + delay;
    mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                      mNextLightAlarmTime, "DeviceIdleController.light", 				     mLightAlarmListener, mHandler);
}
```

## LIGHT_STATE_PRE_IDLE

进入LIGHT_STATE_PRE_IDLE状态

```java
com.android.server.DeviceIdleController#stepLightIdleStateLocked{
 	switch (mLightState) {
        case LIGHT_STATE_INACTIVE:
            mCurIdleBudget = mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
            // Reset the upcoming idle delays.
            //进入下一个状态的延时
            mNextLightIdleDelay = mConstants.LIGHT_IDLE_TIMEOUT;
            mMaintenanceStartTime = 0;
            //检查当前是否还有任务 如果有就进行等待 否则直接进入下一个状态
            if (!isOpsInactiveLocked()) {
                // We have some active ops going on...  give them a chance to finish
                // before going in to our first idle.
                //更新状态
                mLightState = LIGHT_STATE_PRE_IDLE;
                //设置进入下个状态监听
                scheduleLightAlarmLocked(mConstants.LIGHT_PRE_IDLE_TIMEOUT);
                break;
            }
}
//3分钟
LIGHT_PRE_IDLE_TIMEOUT = mParser.getDurationMillis(KEY_LIGHT_PRE_IDLE_TIMEOUT,
                        !COMPRESS_TIME ? 3 * 60 * 1000L : 30 * 1000L);
```

## LIGHT_STATE_IDLE

进入LIGHT_STATE_IDLE状态

```java
1、当STATE_INACTIVE状态在进入下一状态时如果没有活动任务将直接进入LIGHT_STATE_IDLE
2、
void stepLightIdleStateLocked(String reason) {
	switch (mLightState) {
        case LIGHT_STATE_INACTIVE:
        // Nothing active, fall through to immediately idle.
        case LIGHT_STATE_PRE_IDLE:
        case LIGHT_STATE_IDLE_MAINTENANCE:
        	//更改状态
            mLightState = LIGHT_STATE_IDLE;
            addEvent(EVENT_LIGHT_IDLE, null);
            mGoingIdleWakeLock.acquire();
            //通知状态
            mHandler.sendEmptyMessage(MSG_REPORT_IDLE_ON_LIGHT);
    	break;
}
```

## LIGHT_STATE_IDLE_MAINTENANCE

进入LIGHT_STATE_IDLE_MAINTENANCE

```java
case LIGHT_STATE_IDLE:
case LIGHT_STATE_WAITING_FOR_NETWORK:
if (mNetworkConnected || mLightState == LIGHT_STATE_WAITING_FOR_NETWORK) {
    // We have been idling long enough, now it is time to do some work.
    mActiveIdleOpCount = 1;
    mActiveIdleWakeLock.acquire();
    mMaintenanceStartTime = SystemClock.elapsedRealtime();
    if (mCurIdleBudget < mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET) {
        mCurIdleBudget = mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
    } else if (mCurIdleBudget > mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET) {
        mCurIdleBudget = mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET;
    }
    scheduleLightAlarmLocked(mCurIdleBudget);
    if (DEBUG) Slog.d(TAG,
                      "Moved from LIGHT_STATE_IDLE to LIGHT_STATE_IDLE_MAINTENANCE.");
    mLightState = LIGHT_STATE_IDLE_MAINTENANCE;
    EventLogTags.writeDeviceIdleLight(mLightState, reason);
    addEvent(EVENT_LIGHT_MAINTENANCE, null);
    mHandler.sendEmptyMessage(MSG_REPORT_IDLE_OFF);
} else {
    // We'd like to do maintenance, but currently don't have network
    // connectivity...  let's try to wait until the network comes back.
    // We'll only wait for another full idle period, however, and then give up.
    scheduleLightAlarmLocked(mNextLightIdleDelay);
    if (DEBUG) Slog.d(TAG, "Moved to LIGHT_WAITING_FOR_NETWORK.");
    mLightState = LIGHT_STATE_WAITING_FOR_NETWORK;
    EventLogTags.writeDeviceIdleLight(mLightState, reason);
}
break;
```

# 进入深度Doze

进入STATE_INACTIVE条件当前设备没有在充电并且不是强制进入Idle状态，且接收到广播 ACTION_SCREEN_OFF

```java
com.android.server.DeviceIdleController#becomeInactiveIfAppropriateLocked{
	if (mDeepEnabled) {
            if (mQuickDozeActivated) {
                if (mState == STATE_QUICK_DOZE_DELAY || mState == STATE_IDLE
                        || mState == STATE_IDLE_MAINTENANCE) {
                    // Already "idling". Don't want to restart the process.
                    // mLightState can't be LIGHT_STATE_ACTIVE if mState is any of these 3
                    // values, so returning here is safe.
                    return;
                }
                if (DEBUG) {
                    Slog.d(TAG, "Moved from "
                            + stateToString(mState) + " to STATE_QUICK_DOZE_DELAY");
                }
                mState = STATE_QUICK_DOZE_DELAY;
                // Make sure any motion sensing or locating is stopped.
                resetIdleManagementLocked();
                // Wait a small amount of time in case something (eg: background service from
                // recently closed app) needs to finish running.
                scheduleAlarmLocked(mConstants.QUICK_DOZE_DELAY_TIMEOUT, false);
                EventLogTags.writeDeviceIdle(mState, "no activity");
            } else if (mState == STATE_ACTIVE) {
                mState = STATE_INACTIVE;
                if (DEBUG) Slog.d(TAG, "Moved from STATE_ACTIVE to STATE_INACTIVE");
                resetIdleManagementLocked();
                long delay = mInactiveTimeout;
                if (shouldUseIdleTimeoutFactorLocked()) {
                    delay = (long) (mPreIdleFactor * delay);
                }
                scheduleAlarmLocked(delay, false);
                EventLogTags.writeDeviceIdle(mState, "no activity");
            }
        }
}
```

