```
dumpsys package com.android.vending |grep -i -A5 -B5 "runtime permissions"
dumpsys deviceidle whitelist | grep -i $packageName | wc -l
```



```
dumpsys device_policy
```

```
dumpsys deviceidle force-idle //强制让手机进入IDLE模式
dumpsys deviceidle
dumpsys deviceidle whitelist

dumpsys deviceidle enable //让IDLE有效化。
dumpsys deviceidle disable 和 adb shell dumpsys battery reset 即可让手机恢复状态
```


