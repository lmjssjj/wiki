# APN

## APN简介

```
	APN指一种网络接入技术，是通过手机上网时必须配置的一个参数，它决定了手机通过哪种接入方式来访问网络。对于手机用户来说，可以访问的外部网络类型有很多，例如：Internet、WAP网站、集团企业内部网络、行业内部专用网络。而不同的接入点所能访问的范围以及接入的方式是不同的，网络侧如何知道手机激活以后要访问哪个网络从而分配哪个网段的IP呢，这就要靠APN来区分了，即APN决定了用户的手机通过哪种接入方式来访问什么样的网络。
```

## APN分类

### 1、default

```
默认网络连接，当激活时所有数据传输都使用该连接，不能与其他网络连接同时使用
适用场合：绝大部分正常上网时可以使用
```

### 2、mms

```
彩信专用连接，此连接与default类似，用于与载体的多媒体信息服务器对话的应用程序，此连接能与default连接同时使用
适用场合：使用彩信服务时，必须有mms类型的接入点，不必选中，应用程序会自动使用此接入点
```

### 3、supl

```
是SecureUser Plane Location“安全用户面定位”的简写，此连接与default类似，用于帮助定位设备与载体的安全用户面定位服务器对话的应用程序，此连接能与default连接同时使用
```

### 4、dun

```
Dial UpNetworking拨号网络的简称，此连接与default连接类似，用于执行一个拨号网络网桥，使载体能知道拨号网络流量的应用程序，此连接能与default连接同时使用
适用场合：当我们使用自己的手机给别人做热点时使用，不管是USB 热点，wifi热点或则bluetooth热点。将他与default区别开来的主要目的一般是方面计费，国外很多运营商手机自己上网和做热点计费不同的。目前在国内三大运营商都没有区分，所以也就没有dun这个apn
```

### 5、hipri

```
高优先级网络，与default类似，但路由设置不同。使用较少。
```

### 6、ims

```
当ims发起激活请求时会使用这个apn连建立ims的专用承载。
```

### 7、FOTA

```
手机FOTA升级的时候使用
```

### 8.IA

```
IA的apn专用于LTE attach使用，在手机检测到sim卡后，便会加载这个attach apn. 不过很多运营商并没有严格规定attach apn，所以常常复用default类型的apn。在attachapn 加载的时候它有一个优先级顺序，如下：

IaApn > PreferredApn > DefaultApn>FirstApn

IaApn : 类型为ia的apn，优先级最高。

     PreferredApn ：选中的apn。比如在手机setting里面设置的那个apn

     DefaultApn :从apnlist里面查询到的第一个类型为“default”的apn

     FirstApn ：apnlist中的第一个apn。
```

### APN加载和过滤

```
在每次开机的时候系统回自动检查telephony.db是否存在，如果不存在则会创建数据库telephony.db，并利用apns-conf.xml中的内容生成表carriers,以后所有对apn的操作都会是直接针对表carriers，包括查询，创建，修改，删除等。

   当插入一张卡后系统会根据卡的相关信息来匹配相应的apn，在apn list中主要涉及匹配的项有：mcc，mnc，mvno_type, mvno_match_data。mvno_type值决定mvno_match_data的值，android原生代码里mvno_type会有4个值，他们分别是“spn”,“imsi”,“gid”, “iccid”。所以，在apn 读取的时候，会先根据sim卡的mcc，mnc读取出相应的apn list，接着会判断apn list 中的每一个apn的mvno_type 的值，如果不为空，则会根据mvno_type和mvno_match_data再一次对apn list进行过滤，一般情况下，mvno_type,mvno_match_data为空。
```

# MTU最大传输单元（MaximumTransmission Unit）

## MTU简介

```
 mtu是指通信协议的某一层上面所能通过的最大数据包大小（以字节为单位）。最大传输单元这个参数通常与通信接口有关（网络接口卡、串口等）。MTU越大，则一个协议数据单元的承载的有效数据就越长，通信效率也越高。MTU越大，传送相同的用户数据所需的数据包个数也越低。

MTU也不是越大越好，因为MTU越大， 传送一个数据包的延迟也越大；并且MTU越大，数据包中bit位发生错误的概率也越大。MTU越大，通信效率越高而传输延迟增大，所以要权衡通信效率和传输延迟选择合适的MTU。另外，MTU表示的长度包含IP包头的长度，如果IP层以上的协议层发送的数据报文的长度超过了MTU，则在发送者的IP层将对数据报文进行分片，在接收者的IP层对接收到的分片进行重组。在网络通讯中，需要尽量避免发生分片和重组，因为分片重组对网络性能影响较大。数据包发送时选择合适的MTU大小对提高通讯性能很有必要。目前手机在pdn 链接激活时网络返回的MTU大小一般为1500， 但是不用运营商和不用的网络mtu也会改变。MTU值在吞吐量测试中至关重要，也会影响有些多媒体音视频播放的流畅度，所以我必须重视。
```

## Android手机设置mtu的过程

```
在数据激活请求成功后返回的结果中一般会携带mtu的值，如下：
D RILJ  : [6898]> SETUP_DATA_CALL 16 0 data   0 IPV4V6 [SUB0]
D RILJ  : [6898]< SETUP_DATA_CALLDataCallResponse: {version=11 status=0 retry=-1 cid=0 active=2 type=IPV4V6ifname=rmnet_data0 mtu=1500addresses=[10.213.81.9/30] dnses=[212.77.192.59,82.148.111.11]gateways=[10.213.81.10] pcscf=[]} [SUB0]
```

在DataConnection.java中对当前将要使用的mtu再做一次检查

```
/**
     * Read the MTU value from link properties where it can be set from network. In case
     * not set by the network, set it again using the mtu szie value defined in the APN
     * database for the connected APN
     */
    private void checkSetMtu(ApnSetting apn, LinkProperties lp) {
        if (lp == null) return;
        if (apn == null || lp == null) return;

        //如果网络有给出mtu的值则使用网络端给的
        if (lp.getMtu() != PhoneConstants.UNSET_MTU) {
            if (DBG) log("MTU set by call response to: " + lp.getMtu());
            return;
        }
         //如果apn里面有携带mtu则使用
        if (apn != null && apn.mtu != PhoneConstants.UNSET_MTU) {
            lp.setMtu(apn.mtu);
            if (DBG) log("MTU set by APN to: " + apn.mtu);
            return;
        }
```

 //查询资源文件，使用该运营商自定义的mtu值，在做美国项目的时候可能会修改该处代码，将其移到函数最前面，优先使用自定义的mtu

```
int mtu = mPhone.getContext().getResources().getInteger(
                com.android.internal.R.integer.config_mobile_mtu);
        if (mtu != PhoneConstants.UNSET_MTU) {
            lp.setMtu(mtu);
            if (DBG) log("MTU set by config resource to: " + mtu);
        }
    }
```

最后，当数据链接建立起来后会通过ConnectivityService.java中的

private voidupdateMtu(LinkProperties newLp, LinkProperties oldLp)接口更新该值到相应的网络端口上



# 怎样查看和更改手机的MTU值

### 1.用过adb使用ifconfig命令，如下图

```
C:\Users\Administrator\Desktop>adb shell
S5502LA:/ $ ifconfig
wlan0     Link encap:UNSPEC    Driver mt-wifi
          inet addr:192.168.50.87  Bcast:192.168.50.255  Mask:255.255.255.0
          inet6 addr: fe80::1ce9:95ff:fe7f:fe10/64 Scope: Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:1212 errors:0 dropped:0 overruns:0 frame:0
          TX packets:996 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:3000
          RX bytes:506041 TX bytes:273575

dummy0    Link encap:UNSPEC
          inet6 addr: fe80::f004:70ff:fe41:1f20/64 Scope: Link
          UP BROADCAST RUNNING NOARP  MTU:1500  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:12 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:0 TX bytes:840

p2p0      Link encap:UNSPEC    Driver mt-wifi
          UP BROADCAST MULTICAST  MTU:1500  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:0 TX bytes:0

lo        Link encap:UNSPEC
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope: Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:6 errors:0 dropped:0 overruns:0 frame:0
          TX packets:6 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:1080 TX bytes:1080

```



### 2.通过命令更改手机的mtu值

```
更改网卡MTU 值格式： ip link set dev X mtu N 回车 （X=网卡名称，如wlan0，rmnet_data1; N=mtu的值），下面演示更改rment_data7的mtu值：
```

```
S5502LA:/ $ ifconfig lo
lo        Link encap:UNSPEC
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope: Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:6 errors:0 dropped:0 overruns:0 frame:0
          TX packets:6 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:1080 TX bytes:1080

S5502LA:/ $ ip link set dev lo mtu 1500
request send failed: Permission denied
C:\Users\Administrator\Desktop>adb root
restarting adbd as root

C:\Users\Administrator\Desktop>adb remount
remount succeeded

C:\Users\Administrator\Desktop>adb shell
S5502LA:/ # ip link set dev lo mtu 1500

127|S5502LA:/ # ifconfig lo
lo        Link encap:Local Loopback
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope: Host
          UP LOOPBACK RUNNING  MTU:1500  Metric:1
          RX packets:6 errors:0 dropped:0 overruns:0 frame:0
          TX packets:6 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:1080 TX bytes:1080

S5502LA:/ #
```

注意：这个命令修改的值是临时的，链路承载重置，开关机等都会使其还原。

### 3.net log里面查看mtu的值

```
我们先来看一个概念MSS

MSS：maximumsegment size，最大分节大小，为TCP数据包每次传输的最大数据分段大小，一般由发送端向对端TCP通知对端在每个分节中能发送的最大TCP数据

在wireshark中我们可以查找到mss的值，再根据mss换算出mtu，换算公式如下：

MTU = MSS + 20 Byte (IP头部)+20 Byte(TCP头部)， 如：我们查找mss为1460，则相应的mtu大小应为1460+20+20=1500.

在wireshark中一般在tcp三次握手时我都可以看到mss的值。如下图
```

```
7647392 →80[SYN] Seq=0 win=65535 Len=0 MSS=1460 SACK_PERN=1 TSval=475377 TSecr=0 WS=25656 40237→ 80[ACK] Seq=55 Ack=334 win=88832 Len=0
6847392→80[ACK] Seq=1 Ack=1 Win=87808 Len=0 TSval-475382 TSecr=2423962870

```

