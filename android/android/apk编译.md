# aapt - (Android Asset Packaging Tool)

```
	aapt工具编译res资源文件，把大部分xml文件编译成二进制文件（图片文件除外），同时生成R.Java文件和resources.arsc文件，里面保存了资源的ID和在APK中的路径。
```

```
aapt l[ist] [-v] [-a] file.{zip,jar,apk}
```



## R.java

```
R.java文件，这个是通过aapt对资源文件进行编译生成的资源id文件，这样我们程序中使用资源文件更加方便。
```

```java
public final class R {
              ...
    public static final class string {
                     ...
        public static final int app_name=0x7f0a000d;
        public static final int hello_world=0x7f0a000e;
    }
                     ...
}
```

```
可以看到每个资源文件在R中都是一个class，每个资源项名称都分配了一个id，id值是一个四字节无符号整数，格式是这样的：0xpptteeee，（p代表的是package，t代表的是type，e代表的是entry），最高字节代表Package ID，次高字节代表Type ID，后面两个字节代表Entry ID。
```

```
Package ID相当于是一个命名空间，限定资源的来源。Android系统当前定义了两个资源命令空间，其中一个系统资源命令空间，它的Package ID等于0x01，另外一个是应用程序资源命令空间，它的Package ID等于0x7f。所有位于[0x01, 0x7f]之间的Package ID都是合法的，而在这个范围之外的都是非法的Package ID。前面提到的系统资源包package-export.apk的Package ID就等于0x01，而我们在应用程序中定义的资源的Package ID的值都等于0x7f，这一点可以通过生成的R.java文件来验证。

Type ID是指资源的类型ID。资源的类型有animator、anim、color、drawable、layout、menu、raw、string和xml等等若干种，每一种都会被赋予一个ID。

Entry ID是指每一个资源在其所属的资源类型中所出现的次序。注意，不同类型的资源的Entry ID有可能是相同的，但是由于它们的类型不同，我们仍然可以通过其资源ID来区别开来。
```

## Resources.arsc

```
编译后的二进制资源文件。
Resources.arsc对应的数据结构的定义在Android码/frameworks/base/include/androidfw/ResourceType.h中
```

```
aapt d resources XXX.apk
```

# DEX文件

```
	dex是Android系统的可执行文件，包含应用程序的全部操作指令以及运行时数据,由于dalvik是一种针对嵌入式设备而特殊设计的java虚拟机，所以dex文件与标准的class文件在结构设计上有着本质的区别
	当java程序编译成class后，还需要使用dx工具将所有的class文件整合到一个dex文件，目的是其中各个类能够共享数据，在一定程度上降低了冗余，同时也是文件结构更加经凑，实验表明，dex文件是传统jar文件大小的50%左右

```

## 加固原理

![9244906-0a1f1aa3c8f7276e](C:\Users\Administrator\Desktop\wiki\wiki\android\images\9244906-0a1f1aa3c8f7276e.jpg)



### Dex文件整体加固原理如下：

​	![9244906-1c51f29176ecdd62](C:\Users\Administrator\Desktop\wiki\wiki\android\images\9244906-1c51f29176ecdd62.png)