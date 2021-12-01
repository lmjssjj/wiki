## LOCAL_PATH

```
LOCAL_PATH := $(call my-dir)
这句话代表的当前根目录下，每一个Android.mk文件都必须有这个命令而且是在开头
```

## include $(CLEAR_VARS)

```
宏CLEAR_VARS 由编译系统提供，指定让GNU MAKEFILE为你清除许多LOCAL_XXX变量（例如 LOCAL_MODULE, LOCAL_SRC_FILES,LOCAL_STATIC_LIBRARIES, 等等...),除LOCAL_PATH 。
这是必要的，因为所有的编译控制文件都在同一个GNU MAKE执行环境中，所有的变量都是全局的。所以我们每一次设置一个目标文件相关的信息之前都要先清空所有变量的设置，这个也是防止这些变量会影响到我们设置的结果,记住在Android.mk中可以定义多个编译模块，什么叫多个模块？就是我这个文件不知生成apk文件还会生成.so文件或者.a文件，这个后面会讲到，那么这个时候每个编译模块都是以include $(CLEAR_VARS)开始
```

## LOCAL_MODULE_TAGS

```
这个一共有四个值可以设置的LOCAL_MODULE_TAGS ：=user eng tests optional
user: 指该模块只在user版本下才编译
eng: 指该模块只在eng版本下才编译
tests: 指该模块只在tests版本下才编译
optional:指该模块在所有版本下都编译
一般情况下我们都是选择optional
```

## LOCAL_STATIC_JAVA_LIBRARIES 

```
LOCAL_STATIC_JAVA_LIBRARIES := static-library
这个表示的这个APK所依赖的jar包，从上面的代码中可以看出jar包的名字是static-library.jar。
```

## LOCAL_SRC_FILES 

```
LOCAL_SRC_FILES := $(call all-subdir-java-files)
这个就是这个源文件，all-subdir-java-files说明编译这个目录下的所有.java文件，当然这个文件我们也可以手动指定，
比如LOCAL_SRC_FILES :=my.java \  test.java等等，但是这样显然就会比较麻烦
```

## LOCAL_PACKAGE_NAME 

```
LOCAL_PACKAGE_NAME := myTest
这个就是我们要生成这个APK的名字，我们编译好之后这个APK的名字就是myTest.apk，我们可以在源码里面out目录下system/app里面看得到
```

## LOCAL_CERTIFICATE 

```
注：LOCAL_CERTIFICATE 后面应该是签名文件的文件名，比如编译一个需要特殊vendor key签名的APK ，  
LOCAL_CERTIFICATE := vendor/example/certs/app

platform签名：
AndroidManifest.xml的manifest节点中添加　android:sharedUserId=”android.uid.system”，
Android.mk中增加　　
LOCAL_CERTIFICATE := platform
shared签名：
AndroidManifest.xml的manifest节点中增加android:sharedUserId=”android.uid.shared”，
Android.mk中增加
LOCAL_CERTIFICATE := shared
media签名：
AndroidManifest.xml的manifest节点中增加 android:sharedUserId=”android.media”，
Android.mk中增加 
LOCAL_CERTIFICATE := media

LOCAL_CERTIFICATE := testkey   # 普通APK，默认情况下使用
LOCAL_CERTIFICATE := PRESIGNED #app已经签名
```

## LOCAL_PROGUARD_ENABLED 

```
LOCAL_PROGUARD_ENABLED :=disabled
这个就是表示是否要混淆，混淆就是防止别人破解你的代码，这里是不需要要的，如果我们需要混淆就把上面的这句话替换成下面这两句话
```

## LOCAL_PROGUARD_FLAG_FILES

```
LOCAL_PROGUARD_ENABLED :=full
LOCAL_PROGUARD_FLAG_FILES :=proguard.cfg
LOCAL_PROGUARD_FLAG_FILES这个文件指定的proguard.cfg是一个混淆文件，如果我们应用中访问一个已经提供好的jar包接口，但是这个jar包有和JNI打交道的话，我们需要把这个jar包相应的类去掉混淆，java文件和jni打交道一般都是有　public natve......等字样的，如果有那么这个类就不能进行混淆，不然的话就会出现无法运行程序，如何在proguard.cfg去掉指定的混淆呢？，看下面的代码：

－keep public class com.it.cheng.a{*;}这个就是表示对a类去掉混淆的意思，我们就添加在最后就可以了 
```

## include $(BUILD_PACKAGE)

```
这句话就一个执行语句，把上面的设置好的变量开始执行，BUTLD_PACKAGE这个就是表示编译成一个APK，后面还会有编译成库文件等等，这句话介绍就是表示一个模块结束了。我们再来看看编译一个库文件是形式(.so文件)
```

```
include $(BUILD_STATIC_LIBRARY)表示编译成静态库 （*.a）
 include $(BUILD_SHARED_LIBRARY)表示编译成动态库。 
 include $(BUILD_EXECUTABLE)表示编译成可执行程序

在编译库文件的时候可能还会经常加一些条件，比如：
LOCAL_C_INCLUDES 　这个表示加入所需要包含的头文件路径
LOCAL_LDLIBS　本次编译的链接选项，相当于gcc -l后的参数
LOCAL_CFLAGS　同样是编译选项，相当于gcc -O后面的参数
在我们的android源码下一般都是放在system/lib目录下面的，还有一种情况就是第三方的已经把库文件生成好了，只是需要我们放到指定的目录下，如system/lib目录下
```

```
LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS :=optional
LOCAL_MODULE :=liba.so
LOCAL_MODULE_CLASS :=LIB
LOCAL_MODULE_PATH :=$(TARGET_OUT)/lib
LOCAL_SRC_FILES :=liba.so
include $(BUILD_PREBUILT)

上面这段代码的意思就是将一个liba.so库文件放到system／bin目录下的，先说一下这个LOCAL_MODULE 定义的名字，如果是库文件默认为a.so,然后我们在定义LOCAL_MODULE　：＝a.so名字时，在编译完成之后我们在system/lib目录下看到的名字是liba.so,也就是在前面自动的加上了lib作为前缀，但是如果我们在定义LOCAL_MODULE是写出liba.so,那么在编译完成之后就不会在前面自动给你加上的lib的前缀名，关于这个名字的问题还有一个，如果 本地模块指定了LOCAL_MODULE_STEM的话，它的值就 是$(LOCAL_MODULE_STEM)$(LOCAL_MODULE_SUFFIX)，如果没有指定了的话就 是$(LOCAL_MODULE)$(LOCAL_MODULE_SUFFIX)，所以说上面的命名也可以写成这样

LOCAL_MODULE＝liba
LOCAL_MODULE_SUFFIX=.so
```

## LOCAL_MODULE_CLASS /LOCAL_MODULE_PATH

```
LOCAL_MODULE_CLASS := SHARED_LIBRARIES     #放在/system/lib下
LOCAL_MODULE_CLASS := ETC                  #表示放于system/etc目录
LOCAL_MODULE_CLASS := EXECUTABLES          #放于/system/bin
LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)#预置apk到/data/app中
```

```
其实这个的定义就是这样，我们编译完一个文件一般是放在system目录的下的摸个目录里面的，
比如:
如果我放在system/vensor/lib目录下那么我们的LOCAL_MODULE_CLASS可以这样定义为LOCAL_MODULE_CLASS　：＝VENSOR,
放在system／bin目录下我们也可以这样定义为LOCAL_MODULE_CLASS　：＝BIN,
就是说这个值就是system下的一个子目录，要大写，

LOCAL_MODULE_PATH这个值也是指定要安装的目录，我们可以通过给它赋值来强制指定安装的目录，
比如:
安装在system/etc/permissions目录，则可以强制指定它的值为$(TARGET_OUT_ETC)/permissions，这样模块就会被强制安装在这个目录 了，给LOCAL_MODULE_PATH赋值的情况主要应用在prebuilt模块的编译上，其他的应该尽量采用其默认值。至于这两个的区别我也搞得不是很清楚，从其它网站上看是这么解释的：

“。LOCAL_MODULE_PATH是有 个很有用的变量，首先我们看看当我们在本地模块没有指定这个值的时候，它的值实际上是：LOCAL_MODULE_PATH := $($(my_prefix)OUT$(use_data)_$(LOCAL_MODULE_CLASS))，如果你的模块定义了TAGS ：＝ TESTS则user_data的值是DATA，这样的模块会被安装在data/目录下，那么通过替换我们就知道这个LOCAL_MODULE_PATH := TARGET_OUT_$(LOCAL_MODULE_CLASS)。这个LOCAL_MODULE_CLASS在特定的类型编译会被google赋值成 固定内容，但是在prebuilt的编译中它是由你自己来赋值的，它的值就会用来定义生成的目录，
比如LOCAL_MODULE_CLASS := ETC的时候，则就会被安装在/system/etc目录。那么我们就知道如何来定义prebuilt模块里面的CLASS了”

$(TARGET_OUT)表示的就是我们的android源码的system系统目录

TARGET_ROOT_OUT：表示根文件系统out/target/product/generic/root。
TARGET_OUT：表示system文件系统out/target/product/generic/system。
TARGET_OUT_DATA：表示data文件系统out/target/product/generic/data。

这些都是表示不同的变量。

BUILD_PREBUILT表示的就是执行命令的意思。
```

## LOCAL_PRIVILEGED_MODULE 

```
LOCAL_PRIVILEGED_MODULE 决定了其编译后的在ROM中的安装位置：
如果不设置或者设置为false，安装位置为system/app；
如果设置为true，安装位置为system/priv-app
```



## Demo

```
include $(CLEAR_VARS)
LOCAL_MODULE := device_owner_2.xml
LOCAL_MODULE_CLASS := DATA
# This will install the file in /data/system
LOCAL_MODULE_PATH := $(TARGET_OUT_DATA)/system
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)
```



```
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := NuuHelp
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := NuuHelp.apk
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_PRIVILEGED_MODULE := true
include $(BUILD_PREBUILT)
```

```
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE       := device_owner_2
LOCAL_MODULE_TAGS  := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_SRC_FILES    := device_owner_2.xml
LOCAL_MODULE_SUFFIX=.xml
LOCAL_MODULE_PATH  := $(TARGET_OUT_DATA)/system
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE       := device_policies
LOCAL_MODULE_TAGS  := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_SRC_FILES    := device_policies.xml
LOCAL_MODULE_SUFFIX=.xml
LOCAL_MODULE_PATH  := $(TARGET_OUT_DATA)/system

include $(BUILD_PREBUILT)
```

```
###############################################################################
LOCAL_PATH := $(my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := FirstNews
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_PRIVILEGED_MODULE := true
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_SRC_FILES := newsandrewards-stub-v1.2.apk
LOCAL_SDK_VERSION := system_current
LOCAL_PRODUCT_MODULE := true
LOCAL_REQUIRED_MODULES := permission_sliide_us_sliide_newsandrewards.xml

include $(BUILD_PREBUILT)


include $(CLEAR_VARS)
LOCAL_MODULE := permission_sliide_us_sliide_newsandrewards.xml
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_RELATIVE_PATH := permissions
LOCAL_PRODUCT_MODULE := true
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := whitelist_sliide_us_sliide_newsandrewards.xml
#LOCAL_MODULE_OWNER := google
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_PRODUCT_MODULE := true
LOCAL_MODULE_RELATIVE_PATH := sysconfig
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)

```

```
它是直接利用 cp 命令来将要预置的应用拷贝到 out 目录指定位置下以待打包进系统镜像中的：
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
$(shell mkdir -p $(TARGET_OUT_DATA_APPS) )
#直接利用拷贝命令来预置
$(shell cp -af $(LOCAL_PATH)/TsBrowser.apk $(TARGET_OUT_DATA_APPS))
```

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

$(shell mkdir -p $(TARGET_OUT)/app )

#利用拷贝命令来预置
$(shell cp -af $(LOCAL_PATH)/TsBrowser.apk $(TARGET_OUT)/app)
```

```
直接在编译时通过 cp 命令拷贝至你希望它存在的目录就可以了，任何文件都可以以这种方式来预置。

LOCAL_PATH := $(call my-dir)

# ----------------------- script ---------------------------#
include $(CLEAR_VARS)
UM_FILES_PATH = $(LOCAL_PATH)/script
UM_OUT = $(TARGET_OUT)/bin

$(shell mkdir -p $(UM_OUT) )

define find_script_files
$(shell find $(1) -type f -name "*.sh" -exec cp {} $(UM_OUT) \;)
endef

$(call find_script_files, $(UM_FILES_PATH) )

$(shell cp -af $(LOCAL_PATH)/xxx/my_file.file $(TARGET_OUT))

# -----------------------
```

```
编译过程中使用不同于编译的命令来预置了。

不过总得来说，编大包时拷贝的实现也很简单，至少编译系统已经帮我们完成绝大部分的工作了，我们仅仅需要将文件在编译时拷贝至指定目录下即可。
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OUT=$(TARGET_OUT)/bin

$(shell mkdir -p $(OUT) )

define find_files
$(shell find $(1) -name "*.sh" -exec basename {} \; )
endef

LIST=$(call find_files, $(LOCAL_PATH) )

define copy_files
$(foreach t,$(1), \
  $(shell cp $(LOCAL_PATH)/$(t) $(OUT)/$(t) ) \
)
endef

$(call copy_files, $(LIST))
```

