# adb

## 屏幕录制

```
adb shell screenrecord /sdcard/demo.mp4 
开始录制(录制手机屏幕; 视频格式为mp4,存放到手机sd卡里,默认录制时间为180s)
    
	参数: --size
adb shell screenrecord --size 1280*720 /sdcard/demo.mp4
指定视频分辨率大小 (指定分辨率为1280*720;不指定则默认使用手机的分辨率,为获得最佳效果,请使用设备上的高级视频编码（AVC）支持的大小)
    
    参数: --bit-rate
adb shell screenrecord --bit-rate 6000000 /sdcard/demo.mp4 
指定视频的比特率 (指定视频的比特率为6Mbps; 如果不指定则默认为4Mbps, 你可以增加比特率以提高视频质量或为了让文件更小而降低比特率)
    
	参数: --time-limit
adb shell screenrecord  --time-limit 10 /sdcard/demo.mp4 
限制录制时间 (限制视频录制时间为10s; 如果不限制,默认180s)
    
    参数: --verbose
adb shell screenrecord --time-limit 10 --verbose /sdcard/demo.mp4
在命令行显示录屏Log如下:
    xxxx:/ # screenrecord --time-limit 10 --verbose /sdcard/demo.mp4
    Main display is 1080x2160 @60.00fps (orientation=0)
    Configuring recorder for 1080x2160 video/avc at 4.00Mbps
    Content area is 1080x2160 at offset x=0 y=0
    Time limit reached
    Encoder stopping; recorded 34 frames in 10 seconds
    Stopping encoder and muxer
    Executing: /system/bin/am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/demo.mp4
    Broadcasting: Intent { act=android.intent.action.MEDIA_SCANNER_SCAN_FILE dat=file:///sdcard/demo.mp4 }
    Broadcast completed: result=0
    
    
    参数: --bugreport
    添加额外的信息,例如时间戳覆盖,这有助于捕获用于演示错误的视频
    
    参数: --help
    查看帮助
    
    导出视频：
    adb pull /sdcard/demo.mp4
```



```
以下命令执行后会抓取8s systrace然后自动停止：
adb shell atrace -z -t 8 -b 8192 gfx input am wm sched freq idle load binder_driver binder_lock view > settings.atrace
```

