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

