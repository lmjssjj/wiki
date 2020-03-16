#每个Android.mk文件必须以定义LOCAL_PATH为开始。它用于在开发tree中查找源文件。
#宏my-dir 则由Build System提供。返回包含Android.mk的目录路径。
LOCAL_PATH := $(call my-dir)
###############################################################################
# weixin

#CLEAR_VARS 变量由Build System提供。并指向一个指定的GNU Makefile，由它负责清理很多LOCAL_xxx.
#这个清理动作是必须的，因为所有的编译控制文件由同一个GNU Make解析和执行，其变量是全局的。所以清理后才能避免相互影响。
include $(CLEAR_VARS)

# Module name should match apk name to be installed
#LOCAL_MODULE := weixin表示了一个预置的apk在编译中的唯一标识，同时编译后该apk会以此命名。
LOCAL_MODULE := weixin

#这个optional则表示编译到系统中，只有有这个属性就会编译
LOCAL_MODULE_TAGS := optional

#预置apk到data/app/目录并且卸载后恢复出厂可以恢复
#若想不可恢复需要这样配置LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)
LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/operator/app

#表示了当前要预置的apk的文件名，指定 源apk
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk

# 指定文件类型，apk文件用APPS, 并且 会检查 是否是apk文件
#动态库so文件用SHARED_LIBRARIES ，bin文件用EXECUTABLES，其他文件 用ETC
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

#表示 这个apk已经签过名了，系统不需要再次 签名，有时候是LOCAL_CERTIFICATE := platform，platform签名
LOCAL_CERTIFICATE := PRESIGNED

#表示预置到system/priv-app/目录，如果不写这个语句则表示默认编译到system/app/目录
#LOCAL_PRIVILEGED_MODULE := true

#如果预置到system/下的apk并且apk存在so库，则需要手动配置，如果是data/app会自动将自身的lib库抽取安装到自身的根目录
#怎么看有没有so库，只需要解压来看下有没有lib文件夹就好
LOCAL_PREBUILT_JNI_LIBS = \
@lib/armeabi-v7a/libcryptox.so \
@lib/armeabi-v7a/libfb.so 

#表示提取odex文件
LOCAL_DEX_PREOPT := false
include $(BUILD_PREBUILT)
