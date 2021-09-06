echo "请确认手机是否已经连接到电脑"
adb devices

pause

::1.2.1
echo 1.2.1
adb shell am start -n com.mediatek.cellbroadcastreceiver/.CellBroadcastListActivity
adb shell input tap 420 42 
adb shell input tap 204 61 
adb shell input tap 24 223 
adb shell input tap 382 500  
adb shell input tap 24 376  
adb shell input tap 382 500  
adb shell input tap 24 576  
adb shell input tap 382 500  
adb shell input tap 24 682   
adb shell input tap 382 500  
adb shell input tap 24 788  
adb shell input swipe 200 500 200 200  
adb shell input tap 24 457   

TIMEOUT /T 3

adb shell input keyevent KEYCODE_APP_SWITCH
::adb shell input keyevent 20
adb shell input keyevent DEL
TIMEOUT /T 1
 
::1.2.2. BT On
echo 1.2.2 BT On
adb shell am start -a android.bluetooth.adapter.action.REQUEST_ENABLE
adb shell input tap 384 500
TIMEOUT /T 1

::1.2.3 Silent profile
adb shell media volume --show --stream 2 --set 0
adb shell media volume --show --stream 3 --set 0
adb shell media volume --show --stream 4 --set 0
adb shell am start -n com.android.settings/.SoundSettings
TIMEOUT /T 3
adb shell input tap 404 469
adb shell input tap 282 675
adb shell input swipe 200 200 200 600
adb shell input tap 300 180
adb shell input tap 392 800
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 200 200 200 600
adb shell input tap 248 834
TIMEOUT /T 3

::1.2.4 sdcard
echo 1.2.4 sdcard
adb shell am start -a android.settings.INTERNAL_STORAGE_SETTINGS
TIMEOUT /T 5
adb shell input tap 228 618
:: setup 对话框
adb shell input tap 382 520          

adb shell input tap 450 72
adb shell input tap 333 365
adb shell input tap 280 358
adb shell input tap 349	820
::等待格式化
echo 等待格式化
::pause
TIMEOUT /T 15
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 200 200 200 600
adb shell input tap 248 834
TIMEOUT /T 3


::1.2.5 锁屏
echo 1.2.5 锁屏
adb shell am start -n com.android.settings/.SecuritySettings 
TIMEOUT /T 3
adb shell input tap 225 659
adb shell input tap 280 160
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 200 200 200 600
adb shell input tap 248 834
TIMEOUT /T 3


::1.2.7 wifi
echo 1.2.7 wifi
adb shell svc wifi disable


::pause
echo 关闭Find My Device
adb shell am start com.android.settings/com.android.settings.DeviceAdminSettings
TIMEOUT /T 3

adb shell input tap 234 180
TIMEOUT /T 1
adb shell input tap 234 738
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 200 200 200 600
adb shell input tap 248 834
TIMEOUT /T 3
#pause

::1.2.6 dev
echo 1.2.6  更默认usb模式
adb shell am start -a com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS
TIMEOUT /T 3
::确保在最上面
adb shell input swipe 200 200 200 600

adb shell input swipe 200 880 220 50 1000
adb shell input swipe 200 880 220 50 1000
adb shell input swipe 200 880 220 50 1000
adb shell input swipe 200 880 220 50 1000
adb shell input tap 237 440
adb shell input tap 80 400
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 200 200 200 600
adb shell input tap 248 834
TIMEOUT /T 3
::pause
echo  请确认过程中是否有异常，有异常请手动修改
pause

