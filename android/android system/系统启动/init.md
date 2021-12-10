# init

init进程是Android系统在内核启动完毕之后，启动的第一个进程。这个进程会创建运行Android上层所需要的各种运行环境。

```
system/core/init/action.cpp
system/core/init/action_manager.cpp
system/core/init/action_manager.h
system/core/init/action_parser.cpp
system/core/init/action_parser.h
system/core/init/builtins.cpp
system/core/init/import_parser.cpp
system/core/init/init.cpp
system/core/init/keyword_map.h
system/core/init/parser.cpp
system/core/init/service.cpp
system/core/init/tokenizer.cpp
system/core/init/util.cpp
```



## init.rc

```
import 语句表示引入新的 rc文件。
这里需要注意的是部分rc文件的路径是以系统属性的形式体现的。
后续解析 import关键字的时候，是需要将这些系统属性名称替换成具体的属性值。
```

### on

```
on early-init
on init
on post-fs
代码中的 on 表示事件，它的事件名称是 early-init，表示系统在执行 early-init 这个事件的时候触发，触发之后，会执行下面的所有操作，直到遇见下一个事件。
```

on 的格式如下:

on后面跟着一个触发器，当trigger被触发时，command1，command2，command3，会依次执行，直到下一个Action或下一个Service。

```xml
on <trgger> [&& <trigger>]*
   <command1>
   <command2>
   <command3>
```

trigger即我们上面所说的触发器,本质上是一个字符串,能够匹配某种包含该字符串的事件.
trigger又被细分为事件触发器(event trigger)和属性触发器(property trigger).
Triggers（触发器）是一个用于匹配特定事件类型的字符串，用于使Actions发生。

其中部分on事件不止一个条件，可能会有多个条件，通常是 事件名称+系统属性满足的方式触发执行。

比如：启动zygote的事件就是满足三个必要的条件的情况下启动的。

```
# It is recommended to put unnecessary data/ initialization from post-fs-data
# to start-zygote in device's init.rc to unblock zygote start.
on zygote-start && property:ro.crypto.state=unencrypted
    # A/B update verifier that marks a successful boot.
    exec_start update_verifier_nonencrypted
    start netd
    start zygote
    start zygote_secondary

on zygote-start && property:ro.crypto.state=unsupported
    # A/B update verifier that marks a successful boot.
    exec_start update_verifier_nonencrypted
    start netd
    start zygote
    start zygote_secondary

on zygote-start && property:ro.crypto.state=encrypted && property:ro.crypto.type=file
    # A/B update verifier that marks a successful boot.
    exec_start update_verifier_nonencrypted
    start netd
    start zygote
    start zygote_secondary
（在zygote-start事件的情况下，后面系统属性必须满足才可以执行后面的操作）
```

### service

```
service开头的标志，表示当前是一个服务。
Services（服务）是一个程序，以 service开头，由init进程启动，一般运行于另外一个init的子进程，所以启动service前需要判断对应的可执行文件是否存在。
init生成的子进程，定义在rc文件，其中每一个service，在启动时会通过fork方式生成子进程。
Services（服务）的形式如下：
```

```
service <name> <pathname> [ <argument> ]*
    <option>
    <option>
    
其中：
name:服务名
pathname:当前服务对应的程序位置
argument 可选参数
option：当前服务设置的选项
```

```
service zygote /system/bin/app_process32 -Xzygote /system/bin --zygote --start-system-server --socket-name=zygote
    class main
    priority -20
    user root
    group root readproc reserved_disk
    socket zygote stream 660 root system
    socket usap_pool_primary stream 660 root system
    onrestart write /sys/android_power/request_state wake
    onrestart write /sys/power/state on
    onrestart restart audioserver
    onrestart restart cameraserver
    onrestart restart media
    onrestart restart netd
    onrestart restart wificond
    writepid /dev/cpuset/foreground/tasks
```

## **基本架构**

```
rc文件中的关键字有三个，分别是 on ，inport ，以及service。

init进程解析rc文件的时候，通过用三个不同的工具类来分别解析三个不同的关键字所对应的内容，并将解析结果保存。
具体解析的方法如下：
首先定义了一个SectionParser类，类中提供相应的接口函数，然后分别定义 
ImportParser （解析import关键字）
ActionParser（解析on关键字）
ServiceParser（解析service关键字）
解析过程中，根据不同的关键字调用不同的类来进行解析。
```

## **init进程解析rc文件之前的准备工作**

```
https://blog.csdn.net/zhaojigao/article/details/105100620
```

```

```

