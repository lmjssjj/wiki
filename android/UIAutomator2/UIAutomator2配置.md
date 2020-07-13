# UIAutomator2

## 环境配置

### PC环境安装

```
使用python3安装uiautomator2
pip3 install --pre -U uiautomator2
```

### 手机端环境安装

手机链接pc，输入adb命令`adb devices`发现设备后表明设备已连接成功
pc终端输入命令，以安装atx-agent至手机

```
python -m uiautomator2 init
```

## 应用及操作

uiautomator2使用基本过程
 1.选择合适的方式连接手机，如usb数据线，Wi-Fi
 2.使用工具，抓去手机app的控件元素
 3.基于元素控件，调用uiautomator2 API编写UI自动化脚本

### 手机连接方式

#### 1.使用WIFI连接

```python
import uiautomator2 as u2  //依赖包
d = u2.connect('10.242.23.215')
```



#### 2.使用USB连接

```python
import uiautomator2 as u2
d = u2.connect_usb('xxxxx')
```



### 控件识别

使用weditor进行元素识别，
安装方法如下：

```
pip3  install --pre weditor
```



```
使用方法
1.手机连接pc，adb命令保证能正确读取到设备
2.pc终端输入python -m webditor，浏览器自动打开网页http://atx.open.netease.com
3.网页对应位置输入手机设备ip，点击connect连接手机设备，最后根据需要获取手机/app对应元素

正常启动页面如下：
设备id输入/Connect连接/reload刷新页面
```

