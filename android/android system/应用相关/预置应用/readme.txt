#ÿ��Android.mk�ļ������Զ���LOCAL_PATHΪ��ʼ���������ڿ���tree�в���Դ�ļ���
#��my-dir ����Build System�ṩ�����ذ���Android.mk��Ŀ¼·����
LOCAL_PATH := $(call my-dir)
###############################################################################
# weixin

#CLEAR_VARS ������Build System�ṩ����ָ��һ��ָ����GNU Makefile��������������ܶ�LOCAL_xxx.
#����������Ǳ���ģ���Ϊ���еı�������ļ���ͬһ��GNU Make������ִ�У��������ȫ�ֵġ������������ܱ����໥Ӱ�졣
include $(CLEAR_VARS)

# Module name should match apk name to be installed
#LOCAL_MODULE := weixin��ʾ��һ��Ԥ�õ�apk�ڱ����е�Ψһ��ʶ��ͬʱ������apk���Դ�������
LOCAL_MODULE := weixin

#���optional���ʾ���뵽ϵͳ�У�ֻ����������Ծͻ����
LOCAL_MODULE_TAGS := optional

#Ԥ��apk��data/app/Ŀ¼����ж�غ�ָ��������Իָ�
#���벻�ɻָ���Ҫ��������LOCAL_MODULE_PATH := $(TARGET_OUT_DATA_APPS)
LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/operator/app

#��ʾ�˵�ǰҪԤ�õ�apk���ļ�����ָ�� Դapk
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk

# ָ���ļ����ͣ�apk�ļ���APPS, ���� ���� �Ƿ���apk�ļ�
#��̬��so�ļ���SHARED_LIBRARIES ��bin�ļ���EXECUTABLES�������ļ� ��ETC
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

#��ʾ ���apk�Ѿ�ǩ�����ˣ�ϵͳ����Ҫ�ٴ� ǩ������ʱ����LOCAL_CERTIFICATE := platform��platformǩ��
LOCAL_CERTIFICATE := PRESIGNED

#��ʾԤ�õ�system/priv-app/Ŀ¼�������д���������ʾĬ�ϱ��뵽system/app/Ŀ¼
#LOCAL_PRIVILEGED_MODULE := true

#���Ԥ�õ�system/�µ�apk����apk����so�⣬����Ҫ�ֶ����ã������data/app���Զ��������lib���ȡ��װ������ĸ�Ŀ¼
#��ô����û��so�⣬ֻ��Ҫ��ѹ��������û��lib�ļ��оͺ�
LOCAL_PREBUILT_JNI_LIBS = \
@lib/armeabi-v7a/libcryptox.so \
@lib/armeabi-v7a/libfb.so 

#��ʾ��ȡodex�ļ�
LOCAL_DEX_PREOPT := false
include $(BUILD_PREBUILT)
