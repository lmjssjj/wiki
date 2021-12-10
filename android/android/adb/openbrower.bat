@echo off
echo "Please make sure your phone is connected to your computer."  
adb devices
pause
::setlocal enabledelayedexpansion
for /l %%i in (1,1,6) do (
::open browser
echo "%%i times start"
echo open browser
adb shell am start -n com.android.browser/com.android.browser.BrowserActivity 
TIMEOUT /T 5 
::click address bar 
echo click address bar 
adb shell input tap 158 91
TIMEOUT /T 3
::input 
echo Enter the address
adb shell input text https://www.youtube.com/
TIMEOUT /T 1
::enter button
echo press the enter button
adb shell input keyevent 66
TIMEOUT /T 10
::open first item
echo open first item
adb shell input tap 252 400
TIMEOUT /T 25
::click address bar 
echo Enter the address
adb shell input tap 158 91
TIMEOUT /T 3
::input 
echo Enter the address
adb shell input text https://www.engadget.com/
TIMEOUT /T 2
::enter button
echo press the enter button
adb shell input keyevent 66
TIMEOUT /T 10
:: Scroll down
echo scroll down
for /l %%i in (1,1,5) do (
adb shell input swipe 230 750 230 150
TIMEOUT /T 1)
::scroll up
echo scroll up
for /l %%i in (1,1,5) do (
adb shell input swipe 200 200 200 750)
::close
echo close browser
adb shell input keyevent KEYCODE_APP_SWITCH
adb shell input swipe 230 555 230 150
TIMEOUT /T 1
adb shell input keyevent 3
echo "%%i times end")

echo "Completed!" 
pause