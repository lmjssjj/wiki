# 截屏录屏

```
截图录屏相关
MediaProjectionManager,MediaProjection,VirtualDisplay,MediaCodec
```

# 对view的颜色值过滤

```java
//view 颜色过滤相关
Window window = activity.getWindow();
if (window == null) {
    return;
}
View view = window.getDecorView();
Paint paint = new Paint();
ColorMatrix cm = new ColorMatrix();
// 关键起作用的代码，Saturation，翻译成中文就是饱和度的意思。
// 官方文档说明：A value of 0 maps the color to gray-scale. 1 is identity.
// 原来如此，666
cm.setSaturation(0f);
paint.setColorFilter(new ColorMatrixColorFilter(cm));
view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);


// 遍历查找ImageView，对其设置逆矩阵
int childCount = parent.getChildCount();
for (int i = 0; i < childCount; i++) {
    final View childView = parent.getChildAt(i);
    if (childView instanceof ViewGroup) {
        takeOffColor((ViewGroup) childView);
    } else if (childView instanceof ImageView) {
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        childView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }
}
```

# textview跑马灯(reclyview)

```java
1、XML配置

 Textview 一定要用 android:singleLine="true"。因为这个方法过时了，就用  android:lines="1".怎么搞都没用。

<TextView
    android:id="@+id/title"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:ellipsize="marquee"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:marqueeRepeatLimit="marquee_forever"
    android:scrollHorizontally="true"
    android:lines="1"
    android:textSize="12dp" />
正确配置如下：
<TextView
    android:id="@+id/title"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:ellipsize="marquee"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:marqueeRepeatLimit="marquee_forever"
    android:singleLine="true"
    android:scrollHorizontally="true"
    android:textSize="12dp" />
2、在adapter 里面 TextView 设置    holder.titleTv.setSelected(true);
@Override
public void onBindViewHolder(MainListHolder holder, int position) {
    Uri uri = Uri.parse(Constant.IMG_BASEURL + mDatas.get(position).getMainPic());
    Log.i("MainListAdapter", uri.toString());
    holder.draweeView.setImageURI(uri);
    holder.titleTv.setText(mDatas.get(position).getTitle());
    holder.titleTv.setSelected(true);
    holder.cityDsstrictTv.setText(mDatas.get(position).getCityTitle() + " [" +  mDatas.get(position).getDistrictTitle()+"]");
    holder.priceTv.setText(mDatas.get(position).getPrice() + "");
}
```

# 应用崩溃重启

```java
private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LogUtils.e(e.getMessage());
            restartApp();
        }
    };

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }
```

# Userid

```java
mPackageManager = context.getPackageManager();
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mUserId = UserHandle.myUserId();
```

# 系统获取网络时间

```java
//Use the system to get time on the network  
//Below is the system API
public class Utils {
    public void getTime(Context context) {
        new Thread() {
            @Override
            public void run() {
				//NtpTrustedTime system API
                if (NtpTrustedTime.getInstance(context).getCacheAge() > 10000){//Interval between cache time and now
                    boolean b = NtpTrustedTime.getInstance(context).forceRefresh();
                    if (b){
                        long time = NtpTrustedTime.getInstance(context).getCachedNtpTime();
                        //updatatime
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Log.v("lmjssjj",formatter.format(new Date(time)));
                    }
                }
            }
        }.start();
    }
}
```

# 应用数据管理

在程序的manifest文件的application中加上manageSpaceActivity属性，并且指定一个Activity，这个Activity就是点击管理空间之后会跳转的那个Activity了。

```xml
<application android:managespaceactivity="[packageName].ManageSpaceActivity" ...="">  

  
  <activity android:name="[packageName].ManageSpaceActivity" android:screenorientation="portrait">  
</activity></application>  
```

PS 如果要避免数据被删除，可以创建一个自动关闭的Activity。

```java
public class ManageSpaceActivity extends Activity {  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
  
        finish();  
  
    }// onCreate  
} 
```

# 重启app

```java
    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }
```

# 获取指定语言的字符

```java
public String getUsString(){
    String  name;
    //获取当前环境的Resources
    Resources resources = getResources();
    //获得res资源对象
    Configuration config = resources.getConfiguration();
    Locale oldLocale = config.locale;
    //获得设置对象
    DisplayMetrics dm = resources.getDisplayMetrics();
    //  获得屏幕参数：主要是分辨率，像素等。
    config.locale = Locale.ENGLISH;
    // 英文
    resources.updateConfiguration(config, dm);
    name= resources.getString(R.string.app_name);
    config.locale = oldLocale;
    resources.updateConfiguration(config, dm);
    return  name;
}

```

# 判断是否为java进程

```java
public static boolean isJavaProcess(int pid) {
        if (pid <= 0) {
            return false;
        }
        if (bf == null) {
            bf = Process.getPidsForCommands(new String[]{"zygote64", "zygote"});
        }
        if (bf != null) {
            int parentPid = Process.getParentPid(pid);
            for (int i2 : bf) {
                if (parentPid == i2) {
                    return true;
                }
            }
        }
        return false;
    }

```

# 监听事件UEventObserver

使用poll机制来监测我们的事件节点

frameworks\base\core\java\android\os\UEventObserver.java

android.os.UEventObserver

framework中想要监听底层的uevent事件

```java
// watch for invalid charger messages if the invalid_charger switch exists
if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
            UEventObserver invalidChargerObserver = new UEventObserver() {
        //监听uevent事件 对象，用于监听设备插入无效充电的事件，事件发生时会执行回调函数
                @Override
                public void onUEvent(UEvent event) {
                    final int invalidCharger = "1".equals(event.get("SWITCH_STATE")) ? 1 : 0;
                    synchronized (mLock) {
                        if (mInvalidCharger != invalidCharger) {
                            mInvalidCharger = invalidCharger;
                        }
                    }
                }
            };
            invalidChargerObserver.startObserving(
                    "DEVPATH=/devices/virtual/switch/invalid_charger");
}
```

```
eg:
"DEVPATH=/devices/virtual/misc/cpu_loading"
"DEVPATH=/devices/virtual/android_usb/android0"
"DEVPATH=/devices/virtual/misc/usb_accessory"
"DEVPATH=/devices/virtual/misc/mtklmk"
```

